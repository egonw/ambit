var smiles = "";
var jme = "0 0";
var form = "form";

function startEditor(baseRef) {
  // use here fully qualified IP address (number!) i.e.
  // window.open('http://123.456.789.1/jme_window.html',... 
  form ="form";	
  window.open(baseRef+'/jme/jme_window.html','JME','width=500,height=450,scrollbars=no,resizable=yes');
}

function startEditor(baseRef,formName) {
	  // use here fully qualified IP address (number!) i.e.
	  // window.open('http://123.456.789.1/jme_window.html',...
	  form = formName==undefined?'form':formName;
	  window.open(baseRef+'/jme/jme_window.html','JME','width=500,height=450,scrollbars=no,resizable=yes');
}

function fromEditor(smiles,jme) {
  // this function is called from jme_window
  // editor fills variable smiles & jme
  if (smiles=="") {
    alert ("no molecule submitted");
    return;
  }
  document[form].type.value = "smiles"; 
  document[form].search.value = smiles; 
  console
}

function processMolecule() {
  smiles = document[form].search.value;
  if (smiles == "") {alert("Nothing to process!"); return;}
  alert('submitting '+smiles+' for processing!');
  // in actual application remove line above and add something like this
  // document.form.submit();
}

