const repo = 'jeffersonlab/smoothness';
const url = 'https://api.github.com/repos/' + repo + '/contents/?ref=gh-pages';

const list = document.getElementById('dirlist');


function addToList(obj) {
  //console.log('addToList', obj);

  const li = document.createElement("li");
  const a1 = document.createElement("a");
  const a2 = document.createElement("a");
  a1.href = obj.name + '/javadoc/';
  a1.innerText = 'javadoc';
  a2.href = obj.name + '/tlddoc/';
  a2.innerText = 'tlddoc';
  li.appendChild(document.createTextNode(obj.name + ' '));
  li.appendChild(a1);
  li.appendChild(document.createTextNode(' '));
  li.appendChild(a2);
  list.appendChild(li);
  
}

async function fetchData() {
    //console.log('fetchData', url);


    const response = await fetch(url);

    const data = await response.json();

    //console.log(data);

    let dirs = data.filter(function(obj) {
       return obj.type === 'dir';
    });

    
    dirs.sort((a, b) => a.name < b.name ? 1 : -1);


    dirs.forEach(addToList);    
    
}

fetchData();