const express = require('express');
const app = express();

const bodyParser = require('body-parser');
const morgan = require('morgan');

const articleRoutes = require("./api/routes/articles");

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


app.use(morgan('dev'));
app.use(bodyParser.urlencoded({extended:true}));
app.use(bodyParser.json());

module.exports = app;