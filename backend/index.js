var express = require('express')
var dotenv = require('dotenv').config();

var app = express()
 
app.get('/', function(req, res) {
  res.json({data:[
        {lat:40.608962, lng:-75.377874,title:"E.W. Fairchild-Martindale Library"},
        {lat:40.609028, lng:-75.377249,title:"Sinclair Laboratory"},
        {lat:40.607257, lng:-75.374035,title:"Taylor Gymnasium"}
    ]})
})
 
app.listen(process.env.PORT)