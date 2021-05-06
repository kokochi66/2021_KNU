const express = require('express');
const app = express();
let  { PythonShell }  =  require ( 'python-shell' )
let pyshell = new PythonShell('test.py');

var options = {
    mode: 'text',           // 텍스트나 json파일을 주고받기 가능
    pythonPath: '',         // 설치된 파이썬의 경로를 저장, Path에 등록해두었으면 지정할 필요 없음.
    pythonOptions: ['-u'],
    scriptPath: '',
    args: ['value1', 'value2', 'value3']
};

// PythonShell.run('test.py', options, function (err, results) {
// if (err) throw err;
// console.log('results: %j', results);

// }); // python파일을 실행하여 출력하는 코드

app.get('/', (req,res) => {
    
    pyshell.send('hello');
    pyshell.on('message', function (message) {
        // received a message sent from the Python script (a simple "print" statement)
        console.log(message);
    }); // python 파일에 입력을 보낼 수 있는 코드

    res.send('hello nodejs!');
}); // url 접속 시, python 파일이 실행 됨 

app.listen(3000, () => {
    console.log('server connecting :: 3000');
});