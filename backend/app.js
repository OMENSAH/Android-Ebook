const express = require('express');
const app = express();

const bodyParser = require('body-parser');
const morgan = require('morgan');
const cors = require("cors");
const articleRoutes = require("./api/routes/articles");
const mongoose = require('mongoose');

mongoose.connect(
    'mongodb://olivermensah:12345digest@ds157422.mlab.com:57422/digest'
).then(res=>console.log("Connnected")).catch(err=> console.log(err));


app.use(morgan('dev'));
app.use(bodyParser.urlencoded({extended:false}));
app.use(bodyParser.json());
app.use(cors());

app.use('/articles', articleRoutes);

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