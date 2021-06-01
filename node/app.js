const express = require('express');
const fs = require('fs')
const app = express();
let  { PythonShell }  =  require ( 'python-shell' )
let pyshell = new PythonShell('test.py');


    // pyshell.send(base64str);
    // pyshell.on('message', function (message) {
    //     console.log(message)
    // }); // python 파일에 입력을 보낼 수 있는 코드


app.get('/', (req,res) => {
    var base64str = base64_encode('img.png');
    let options = {
        mode: 'text',
        pythonPath: '', // Python의 경로를 나타낸다
        pythonOptions: ['-u'], // get print results in real-time
        scriptPath: '', // Python Script가 있는 경로
        args: base64str // Python Script에 넘겨줄 인자 목록
      };

    PythonShell.run('test.py', options, function (err, results) {
        if (err) throw err;
        res.send([{"result":results}]);
    }); // python파일을 실행하여 출력하는 코드
    
}); // url 접속 시, python 파일이 실행 됨 

app.listen(3000, () => {
    console.log('server connecting :: 3000');
});


function base64_encode(file) {   
    var bitmap = fs.readFileSync(file);  
    return new Buffer(bitmap).toString('base64');  
}  
  
function base64_decode(base64str, file) {  
    var bitmap = new Buffer(base64str, 'base64');  
    fs.writeFileSync(file, bitmap);  
}  
  

