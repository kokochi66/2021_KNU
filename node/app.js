const app = require('express')()
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const fs = require('fs');  
const { PythonShell } = require('python-shell');
var ocrPath = './img/cropped'

let pythonFile = './test.py'
let pythonFile2 = './CRAFT-pytorch-master/test.py'
let pyshell = new PythonShell(pythonFile2)


// 서버와 모델간의 직접적인 통신을 테스트하는 서버파일

app.listen(3000, () => {
    console.log("server connecting :: 3000")

    let imageFile = fs.readFileSync('./sample/img3.jpg')
    let base64Image = base64_encode(imageFile)
    let decodedImage = base64_decode(base64Image)

    let options = {
        args: ['app.js input data String']
      };
      pyshell.send(base64Image)
      pyshell.on('message', (message) => {
        console.log('send messgae :',message)
      })

      pyshell.end((err, code, signal) => {
        if(err) throw err;
        console.log('the exit code was :', code)
        console.log('the exit signal was :', signal)
        console.log('finished')
      })

})

// base64 인코딩
function base64_encode(file) {  
    return new Buffer.from(file).toString('base64');  
  }  
  
  // base64 디코딩
  function base64_decode(base64str) {  
    var bitmap = new Buffer.from(base64str, 'base64');
    return bitmap
  }  