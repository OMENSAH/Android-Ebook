const jwtCheck  = require("../middleware/check-auth");
const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');
const Article = require('../../models/article');

router.get("/all-articles", jwtCheck,(req, res, next) => {
    Article.find()
    .exec()
    .then(docs => {
        const response = docs.map(doc => {
                return {
                    title: doc.title,
                    author: doc.author,
                    _id: doc._id,
                    featuredImage: doc.featuredImage,
                    body: doc.body,
                };
            });
        res.status(200).json(response);
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });
});

router.get("/article-details/:articleId", jwtCheck,(req,res,next) => {
    const id = req.params.articleId;
    Article.find({_id:id})
    .exec()
    .then(docs => {
        const response = {
            book: docs.map(doc => {
                return {
                  title: doc.title,
                  author: doc.author,
                  _id: doc._id,
                  featuredImage: doc.featuredImage,
                  body: doc.body,
              };
            })
        };
        res.status(200).json(response);
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error:err
        });
    });
});

router.post("/add-article", jwtCheck, (req, res, next) => {
    const article = new Article({
        _id: new mongoose.Types.ObjectId(),
        title:req.body.title,
        author:req.body.author,
        featuredImage: req.body.featuredImage,
        body:req.body.body,
    });
    article
    .save()
    .then(result => {
        console.log(result);
        res.status(201).json({
            message:"Article successfully added",
        });
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error:err
        });
    });
});
module.exports = router;
  