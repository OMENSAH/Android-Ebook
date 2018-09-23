const jwtCheck  = require("../middleware/check-auth");
const express = require('express');
const router = express.Router();
const mongoose = require("mongoose");
const multer = require("multer");
const Article = require('../../models/article');
// Set The Storage Engine
let storage	=	multer.diskStorage({
    destination: function (req, file, callback) {
      callback(null, './uploads');
    },
    filename: function (req, file, callback) {
      callback(null, file.originalname);
    }
});
let upload = multer({ storage : storage }).any();


router.get("/all-articles", jwtCheck,(req, res) => {

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
                    posted_on:doc.posted_on
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

router.get("/article-details/:articleId", jwtCheck,(req,res) => {
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
                  posted_on: doc.posted_on
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
router.post("/add-article", jwtCheck, (req, res) => {
    upload(req, res, (err) => {
        if(err){
            res.status(500).json({
                error:err
            });
        } else {
          if(req.files == undefined){
            res.status(500).json({
                error:"No File Selected"
            });
          } else {
                console.log(req.files)
                const article = new Article({
                    _id: new mongoose.Types.ObjectId(),
                    title:req.body.title,
                    author:req.body.author,
                    featuredImage: req.files[0].originalname,
                    body:req.body.body,
                    posted_on: req.body.posted_on
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
            }
        }
    });
});
module.exports = router;
  