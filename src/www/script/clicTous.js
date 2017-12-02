function clicTous(form,booleen)
  {
  for (i=0, n=form.elements.length; i<n; i++)
  if (form.elements[i].name.indexOf('moteur') != -1)
    form.elements[i].checked = booleen;
  }
