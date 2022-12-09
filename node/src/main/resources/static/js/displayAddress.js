
var listAddress = document.querySelector('#tableAddress');



function start()
{
    getAddress();
    
}
start();

function getAddress()
{
    var myApi = 'https://jsonplaceholder.typicode.com/todos/1';
    fetch(myApi).then(function(response){
        console.log(response.json());
    })

 
//     fetch('http://localhost:8080/address')
//   .then((response) => response.json())
//   .then((data) => console.log(data));
}