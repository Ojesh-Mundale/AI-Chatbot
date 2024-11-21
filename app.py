import os
from flask import Flask, render_template, request, jsonify
import google.generativeai as genai

# Configure the API key using the correct environment variable name
genai.configure(api_key=os.environ["GEMINI_API_KEY"])


# Create the Flask app
app = Flask(__name__)

# Create the model
generation_config = {
    "temperature": 0.7,
    "top_p": 0.9,
    "top_k": 20,
    "max_output_tokens": 100,
    "response_mime_type": "text/plain",
}

model = genai.GenerativeModel(
    model_name="gemini-1.5-flash",
    generation_config=generation_config,
)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/chat', methods=['POST'])
def chat():
    user_input = request.form['user_input']
    
    # Start a new chat session or use an existing one
    chat_session = model.start_chat(
        history=[
            {
                "role": "user",
                "parts": [user_input + "\n"],
            },
        ]
    )
    
    # Send the user's message and get the response
    response = chat_session.send_message(user_input)
    
    return jsonify({'response': response.text})

if __name__ == '__main__':
    app.run(debug=True)