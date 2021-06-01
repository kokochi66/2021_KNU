const app = require('express')();
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const fs = require('fs');  
const { PythonShell } = require('python-shell');
var ocrPath = './img/cropped' // OCR 이미지 경로

// 현재 작업 디렉토리를 node.js 파일이 있는 곳으로 변경
// 명령 프롬프트 사용시 해당 경로로 이동 안하고 node 실행 시 경로 오류 방지
process.chdir(__dirname)

io.on('connection', (socket) => {
  console.log("user connected");

  socket.on('connectReceive', (data) => {
  	console.log(data)
  });

  // image 타입 데이터 수신
  socket.on('image', (data) => {
    console.log("image access")
  	base64_decode(data, './img/res.jpg')

    // 이미지 폴더 초기화 작업 (이미지 넘어올때 마다 결과 폴더 초기화)
    if (!fs.existsSync(ocrPath)) {
      fs.mkdir(ocrPath, err=> {
        if (err) throw err;
      }) 
    }
    else {
      fs.readdirSync(ocrPath).forEach(function(exfile, index){   
        var curPath = ocrPath + "/" + exfile;
        fs.unlinkSync(curPath);
      });
    }

    // 원본 이미지에 경계 박스 표시 + 자른 이미지 저장
    PythonShell.run('./CRAFT-pytorch-master/test.py', null, (err, results) => {
      if (err) throw err;

      var d = base64_encode('./CRAFT-pytorch-master/result/res_res.jpg'); // 경계 박스 결과 경로
      console.log('craft complete')
      socket.emit('image', d)

      // 자른 이미지 OCR
      PythonShell.run('./deep-text-recognition-benchmark-master/demo.py', null, (err, results2) => {
        if (err) throw err;
        console.log('OCR complete');
        var ocrres = fs.readFileSync("result.json", 'utf-8'); // OCR 결과 파일 그대로 전송(string) 
        socket.emit('ocr', ocrres)
      });
    });
  });

  socket.on('disconnect', function() {
    console.log('user disconnected');
  });
});

server.listen(3000, function(){
  console.log("server on : 3000");
});

// base64 인코딩
function base64_encode(file) {  
  var bitmap = fs.readFileSync(file);  
  return new Buffer.from(bitmap).toString('base64');  
}  

// base64 디코딩
function base64_decode(base64str, file) {  
  var bitmap = new Buffer.from(base64str, 'base64');  
  fs.writeFileSync(file, bitmap);
}  