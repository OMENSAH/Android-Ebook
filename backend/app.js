const express = require('express');
const app = express();
const morgan = require('morgan');
const cors = require("cors");
const articleRoutes = require("./api/routes/articles");
const mongoose = require('mongoose');
const bodyParser =	require("body-parser");
require('dotenv').config();

if (!process.env.DB) {
  throw 'Make sure you have DB in your .env file';
}
mongoose.connect(
    process.env.DB
).then(res=>console.log("Connnected")).catch(err=> console.log(err));

app.use(morgan('dev'));
app.use(cors());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json()); 
app.use(express.static('./uploads'));

app.use('/', articleRoutes);

app.use((req,res,next) => {
    const error = new Error("Not Found");
    error.status = 404;
    next(error);
})

app.use((error, req, res, next) => {
    console.log(error.message)
    res.status(error.status || 500);
    res.json({
        error:{
            message:error.message
        }
    });
});


module.exports = app;