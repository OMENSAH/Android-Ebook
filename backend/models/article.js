const mongoose = require('mongoose');

const commentSchema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    body: {
        type: String,
        required: true
    }, 
    posted_on: String
});
mongoose.model('Comment', commentSchema);

const articleSchema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    title:{
        type:String,
        required:true,
        unique:true,
    },
    author:String,
    featuredImage:String,
    body: {
        type: String,
        required: true
    }, 
    posted_on: String,
    comments : [
        { 
            type: mongoose.Schema.Types.ObjectId, 
            ref: 'Comment' 
        }
    ]
});

module.exports = mongoose.model('Article', articleSchema);