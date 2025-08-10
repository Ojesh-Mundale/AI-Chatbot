package com.codementor.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessingService.class);
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_PDF_PAGES = 100; // Limit PDF processing to prevent memory issues
    private static final int MAX_TEXT_LENGTH = 50000; // Limit extracted text length

    public String extractTextFromDocument(MultipartFile file) throws IOException {
        logger.info("Processing file upload: {} (size: {} bytes, content-type: {})", 
                   file.getOriginalFilename(), file.getSize(), file.getContentType());
        
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                String.format("File too large. Maximum size is %dMB", MAX_FILE_SIZE / (1024 * 1024)));
        }
        
        // Validate file is not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        
        try {
            // Pre-validate file size again to prevent processing very large files
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File exceeds maximum allowed size");
            }
            
            if (contentType != null) {
                if (contentType.equals("application/pdf") || 
                    (contentType.equals("application/octet-stream") && filename != null && filename.toLowerCase().endsWith(".pdf"))) {
                    return extractTextFromPDF(file);
                } else if (contentType.startsWith("text/") || 
                          (contentType.equals("application/octet-stream") && filename != null && 
                           (filename.toLowerCase().endsWith(".txt") || filename.toLowerCase().endsWith(".text")))) {
                    return extractTextFromTextFile(file);
                }
            }
            
            // Try to determine file type by extension
            if (filename != null) {
                String lowerFilename = filename.toLowerCase();
                if (lowerFilename.endsWith(".pdf")) {
                    return extractTextFromPDF(file);
                } else if (lowerFilename.endsWith(".txt") || lowerFilename.endsWith(".text")) {
                    return extractTextFromTextFile(file);
                }
            }
            
            throw new IllegalArgumentException(
                "Unsupported file type. Please upload PDF (.pdf) or text (.txt) files.");
            
        } catch (OutOfMemoryError e) {
            logger.error("Out of memory while processing file: {}", filename, e);
            System.gc(); // Suggest garbage collection
            throw new IllegalArgumentException(
                "File too large to process. Please try a smaller file or split the document.");
        } catch (Exception e) {
            logger.error("Error processing file: {}", filename, e);
            throw new IOException("Failed to process document: " + e.getMessage());
        }
    }

    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {
            
            int pageCount = document.getNumberOfPages();
            logger.info("Processing PDF with {} pages", pageCount);
            
            if (pageCount > MAX_PDF_PAGES) {
                throw new IllegalArgumentException(
                    String.format("PDF too large (%d pages). Maximum supported is %d pages.", 
                                pageCount, MAX_PDF_PAGES));
            }
            
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            
            // For large PDFs, extract only first few pages to prevent memory issues
            if (pageCount > 20) {
                stripper.setStartPage(1);
                stripper.setEndPage(Math.min(pageCount, 20));
                logger.warn("Large PDF detected, extracting first {} pages only", Math.min(pageCount, 20));
            }
            
            String text = stripper.getText(document);
            
            // Limit text length to prevent memory issues
            if (text.length() > MAX_TEXT_LENGTH) {
                text = text.substring(0, MAX_TEXT_LENGTH);
                logger.warn("Text truncated to {} characters due to length limit", MAX_TEXT_LENGTH);
            }
            
            logger.info("Successfully extracted {} characters from PDF", text.length());
            return text.trim();
        }
    }
    
    private String extractTextFromTextFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] bytes = inputStream.readAllBytes();
            String content = new String(bytes, "UTF-8");
            
            // Limit text length
            if (content.length() > MAX_TEXT_LENGTH) {
                content = content.substring(0, MAX_TEXT_LENGTH);
                logger.warn("Text file truncated to {} characters due to length limit", MAX_TEXT_LENGTH);
            }
            
            logger.info("Successfully extracted text file with {} characters", content.length());
            return content.trim();
        }
    }

    public boolean isValidFileType(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        String lowerFilename = filename.toLowerCase().trim();
        return lowerFilename.endsWith(".pdf") || 
               lowerFilename.endsWith(".txt") || 
               lowerFilename.endsWith(".text");
    }
    
    public boolean isValidFileSize(long size) {
        return size > 0 && size <= MAX_FILE_SIZE;
    }
}
