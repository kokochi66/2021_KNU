const app = require('express')()
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const fs = require('fs');  
const { PythonShell } = require('python-shell');
var ocrPath = './img/cropped'

// 서버와 모델간의 직접적인 통신을 테스트하는 서버파일

app.listen(3000, () => {
    console.log("server connecting :: 3000")

    let imageFile = fs.readFileSync('./sample/img2.jpg')
    let base64Image = base64_encode(imageFile)
    let decodedImage = base64_decode(base64Image)
    fs.writeFileSync('./savedImage.jpg', decodedImage, (err) => {});

    let options = {
        mode : 'text',
        args: ['app.js input data String']
      };
  
      let pythonFile = './test.py'
      let pythonFile2 = './CRAFT-pytorch-master/test.py'
  
      // 원본 이미지에 경계 박스 표시 + 자른 이미지 저장
      PythonShell.run(pythonFile2, options, (err, result) => {
        if (err) throw err;
      });

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