function clicTous(form,booleen)
  {
  for (let i=0,let n=form.elements.length; i<n; i++)
  if (form.elements[i].name.indexOf('moteur') != -1)
    form.elements[i].checked = booleen;
  }
