const mongoose = require('mongoose');
const articleSchema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    title:{
        type:String,
        required:true,
        unique:true,
    },
    author:String,
    featuredImage: String,
    body: {
        type: String,
        required: true
    }
});

module.exports = mongoose.model('Article', articleSchema);