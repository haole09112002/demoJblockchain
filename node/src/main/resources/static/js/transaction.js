function start()
{
    getTransaction();
    handleCreate();
}



start();

function getTransaction()
{
    const myRequest = new Request('http://127.0.0.1:8080/transaction', {
    method: 'GET',
    headers: {'Content-Type': 'application/json'},
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
            <td>${item.text} </td>
            <td>${item.senderHash} </td>
            <td>${item.signature} </td>
            <td>${item.timestamp} </td>
        </tr>`
    })
    var listAddress = document.querySelector('#transactionPool');
    listAddress.innerHTML = htmls.join(' ');
}

function createTransaction(data)
{
    url = 'http://127.0.0.1:8080/transaction';
    fetch(url, {
       method: 'PUT', // *GET, POST, PUT, DELETE, etc.
       headers: {
         'Content-Type': 'application/json'
       },
       body: JSON.stringify(data) // body data type must match "Content-Type" header
     });
}


function handleCreate()
{
    var creatBtn =  document.querySelector('#create');
    creatBtn.onclick = function()
    {
        var sender = document.querySelector('input[name="sender"]').value;
        // var receiver = document.querySelector('input[name="receiver"]').value;
        var message = document.querySelector('input[name="message"]').value;
        var privateKey = document.querySelector('input[name="privateKey"]').value;
        var data = {text: message,
                    senderHash : sender,
                    signature : privateKey
        }
        // alert();
        console.log(data);
        createTransaction(data);
    }
}