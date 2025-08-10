# Edumate with CodeMentor Chatbot

A sophisticated AI-powered chatbot application built with Java Spring Boot that provides educational assistance, document processing, and AI-driven features for students and educators.

## 🚀 Features

### Core Chatbot Capabilities
- **Natural Language Processing**: Advanced AI responses using Gemini API
- **Multi-modal Support**: Text-based conversations with intelligent context understanding
- **Real-time Communication**: WebSocket-based instant messaging

### Educational Tools
- **EduMate Module**: Specialized educational assistance interface
- **Document Processing**: Upload and analyze PDF, DOCX, and text documents
- **Quiz Generation**: AI-powered quiz creation from study materials
- **Study Assistant**: Personalized learning recommendations

### Technical Features
- **Responsive Web Interface**: Modern, mobile-friendly UI
- **RESTful API**: Clean API endpoints for all functionalities
- **Exception Handling**: Global error handling with user-friendly messages
- **Configuration Management**: Environment-based configuration

## 🛠️ Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.x** - Application framework
- **Spring Web** - REST API development
- **Spring WebSocket** - Real-time communication
- **Gemini API** - AI/ML integration

### Frontend
- **Thymeleaf** - Server-side template engine
- **HTML5/CSS3/JavaScript** - Modern web technologies
- **Responsive Design** - Bootstrap framework

### Build & Deployment
- **Maven** - Build automation
- **JAR Packaging** - Self-contained executable

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Gemini API key

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd your_file_name
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

4. **Access the application**
- Main Chatbot: http://localhost:8080
- EduMate Interface: http://localhost:8080/edumate

# Gemini API Configuration
gemini.api.key=your-api-key-here
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Google Gemini API for AI capabilities
- Spring Boot team for the excellent framework
- Thymeleaf for server-side templating

