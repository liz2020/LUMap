var express = require('express')
var dotenv = require('dotenv').config();

var app = express()
 
app.get('/', function(req, res) {
  res.json({data:[
        {lat:40.608962, lng:-75.377874,title:"E.W. Fairchild-Martindale Library"},
        {lat:40.609028, lng:-75.377249,title:"Sinclair Laboratory"},
        {lat:40.607412, lng:-75.376702,title:"Chandler-Ullmann Hall"},
        {lat:40.606866, lng:-75.380157,title:"Alumni Memorial Building"},
        {lat:40.607741, lng:-75.377712,title:"Packer Memorial Church"},
        {lat:40.608356, lng:-75.378935,title:"STEPS"},
        {lat:40.608596, lng:-75.376191,title:"Whitaker Laboratory"},
        {lat:40.608253, lng:-75.373867,title:"Rauch Business Center"},
        {lat:40.607259, lng:-75.374039,title:"Taylor Gymnasium"},
        {lat:40.607585, lng:-75.375007,title:"Sherman Fairchild Center for the Physical Sciences"},
        {lat:40.606723, lng:-75.375583,title:"Williams Hall"},
        {lat:40.606707, lng:-75.376972,title:"Linderman Library"},
        {lat:40.606228, lng:-75.378156,title:"University Center"},
        {lat:40.607919, lng:-75.378949,title:"Packard Laboratory"}
    ]})
})
 
app.listen(process.env.PORT)