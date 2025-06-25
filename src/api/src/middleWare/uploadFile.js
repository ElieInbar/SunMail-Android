const multer = require('multer');
const path = require('path');
const fs = require('fs')

// Create uploads directory if it doesn't exist
const uploadsDir = path.join(__dirname, '../uploads');
if (!fs.existsSync(uploadsDir)) {
    fs.mkdirSync(uploadsDir, { recursive: true });
}

const ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
const MAX_FILE_SIZE = 10 * 1024 * 1024; 

// Configure storage
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        if (file.fieldname === 'profilePicture') {
            cb(null, uploadsDir); 
        }
    },
    filename: (req, file, cb) => {
        cb(null, `${Date.now()}_${file.originalname}`);
    }
});

// File filter
const fileFilter = (req, file, cb) => {
    if (file.fieldname !== 'profilePicture') {
        cb(new Error('Invalid field name. Expected "profilePicture".'), false);
        return;
    }

    // Check file size
    if (file.size > MAX_FILE_SIZE) {
        cb(new Error('File size too large. Maximum size is 10MB.'), false);
        return;
    }

    // Check file type
    if (!file.mimetype.startsWith('image/')) {
        cb(new Error('File must be an image.'), false);
        return;
    }

    // Check specific image formats
    if (!ALLOWED_IMAGE_TYPES.includes(file.mimetype)) {
        cb(new Error('Unsupported image format. Please use JPEG, PNG, GIF, or WebP.'), false);
        return;
    }

    // If all checks pass
    cb(null, true);
};

const upload = multer({
    storage,
    fileFilter,
    limits: {
        fileSize: MAX_FILE_SIZE,
        files: 1 // Limit to one file
    }
});

module.exports = upload;