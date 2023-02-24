const repo = 'jeffersonlab/smoothness';
const url = 'https://api.github.com/repos/' + repo + '/contents/?ref=gh-pages';

const list = document.getElementById('dirlist');

function sortSemVer(arr, reverse = false) {
    let semVerArr = arr.map(i => i.replace(/(\d+)/g, m => +m + 100000)).sort();
    if (reverse)
        semVerArr = semVerArr.reverse();

    return semVerArr.map(i => i.replace(/(\d+)/g, m => +m - 100000));
}


function addToList(name) {
  //console.log('addToList', name);

  const li = document.createElement("li");
  const a1 = document.createElement("a");
  const a2 = document.createElement("a");
  a1.href = name + '/javadoc/';
  a1.innerText = 'javadoc';
  a2.href = name + '/tlddoc/';
  a2.innerText = 'tlddoc';
  li.appendChild(document.createTextNode(name + ' '));
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

    
    let names = dirs.map(i => i.name);

    
    //console.log(names);

    sorted = sortSemVer(names, true);


    sorted.forEach(addToList);    
    
}

fetchData();