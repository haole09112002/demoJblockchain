

const myHeaders = new Headers();





function start()
{
    getAddress();
    handleCreate();
}
start();

function getAddress()
{
    const myRequest = new Request('http://127.0.0.1:8080/address', {
  method: 'GET',
headers: {myHeaders ,'Content-Type': 'application/json'},
//   mode: 'cors',
  cache: 'default',
  
});
    fetch(myRequest)
  .then((response) => { return response.json()})
  .then((data) =>  renderData(data));

}
function renderData(data){
    
    var htmls = data.map(function(item)
    {
        return `<tr>
            <td>${item.hash} </td>
            <td>${item.name} </td>
            <td>${item.publicKey} </td>
        </tr>`
    })
    var listAddress = document.querySelector('#tableAddress');
    listAddress.innerHTML = htmls.join(' ');
}

function createAddress(data)
{
    url = 'http://127.0.0.1:8080/address';
     fetch(url, {
        method: 'PUT', // *GET, POST, PUT, DELETE, etc.
        // mode: 'cors', // no-cors, *cors, same-origin
        // cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        // credentials: 'same-origin', // include, *same-origin, omit
        headers: {
          'Content-Type': 'application/json'
          // 'Content-Type': 'application/x-www-form-urlencoded',
        },
        // redirect: 'follow', // manual, *follow, error
        // referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify(data) // body data type must match "Content-Type" header
      });
}

function handleCreate()
{
    var creatBtn =  document.querySelector('#create');
    creatBtn.onclick = function()
    {
        var publicKey = document.querySelector('input[name="publicKey"]').value;
        var name = document.querySelector('input[name="name"]').value;
        var data = {publicKey: publicKey,
                    name : name
        }
        console.log(data);
        createAddress(data);
    }
}