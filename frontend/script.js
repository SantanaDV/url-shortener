/**
 * URL Shortener Frontend Logic
 * Integrates with the Spring Boot backend hosted on Railway.
 */

document.addEventListener('DOMContentLoaded', () => {
    // DOM Elements
    const form = document.getElementById('shorten-form');
    const urlInput = document.getElementById('url-input');
    const submitBtn = document.getElementById('submit-btn');
    const btnText = document.querySelector('.btn-text');
    const loader = document.querySelector('.loader');
    
    const errorMessage = document.getElementById('error-message');
    const resultContainer = document.getElementById('result-container');
    const shortUrlLink = document.getElementById('short-url-link');
    const copyBtn = document.getElementById('copy-btn');
    const copyNotification = document.getElementById('copy-notification');

    // API Configuration
    const API_URL = 'https://url-shortener-production-d7cf.up.railway.app/api/shorten';

    // Handle form submission
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const originalUrl = urlInput.value.trim();
        
        if (!originalUrl) {
            showError('Please enter a valid URL.');
            return;
        }

        // Basic client-side validation
        if (!originalUrl.startsWith('http://') && !originalUrl.startsWith('https://')) {
            showError('URL must start with http:// or https://');
            return;
        }

        // Reset UI state before request
        hideError();
        setLoading(true);
        resultContainer.classList.add('hidden');

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({ url: originalUrl })
            });

            const data = await response.json();

            if (!response.ok) {
                // Handle backend errors (e.g., 400 Bad Request)
                throw new Error(data.message || data.error || 'Failed to shorten URL');
            }

            // Success
            showResult(data.shortUrl);

        } catch (error) {
            console.error('Error shortening URL:', error);
            showError(error.message || 'An unexpected error occurred. Please try again.');
        } finally {
            setLoading(false);
        }
    });

    // Handle Copy to Clipboard
    copyBtn.addEventListener('click', async () => {
        const shortUrl = shortUrlLink.href;
        
        try {
            await navigator.clipboard.writeText(shortUrl);
            
            // Show notification toast
            copyNotification.classList.remove('hidden');
            
            // Hide notification after 2 seconds
            setTimeout(() => {
                copyNotification.classList.add('hidden');
            }, 2000);
            
        } catch (err) {
            console.error('Failed to copy text: ', err);
            // Fallback for older browsers could go here
            alert('Failed to copy to clipboard. Please manually select and copy.');
        }
    });

    // --- Helper Functions ---

    function setLoading(isLoading) {
        if (isLoading) {
            btnText.classList.add('hidden');
            loader.classList.remove('hidden');
            submitBtn.disabled = true;
            urlInput.disabled = true;
        } else {
            btnText.classList.remove('hidden');
            loader.classList.add('hidden');
            submitBtn.disabled = false;
            urlInput.disabled = false;
            
            // Refocus input for better UX
            urlInput.focus();
        }
    }

    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.classList.remove('hidden');
    }

    function hideError() {
        errorMessage.textContent = '';
        errorMessage.classList.add('hidden');
    }

    function showResult(shortUrl) {
        // Handle case where backend might return localhost in the shortUrl (from testing)
        // If the backend returns localhost, let's swap it to the live domain so it actually works for people.
        let finalUrl = shortUrl;
        
        // Safety check if the backend is misconfigured and returning local URLs
        if (finalUrl.includes('localhost:')) {
            const hash = finalUrl.substring(finalUrl.lastIndexOf('/') + 1);
            finalUrl = `https://url-shortener-production-d7cf.up.railway.app/${hash}`;
        }
        
        shortUrlLink.href = finalUrl;
        shortUrlLink.textContent = finalUrl;
        resultContainer.classList.remove('hidden');
        
        // Clear input field on success
        urlInput.value = '';
    }
});
