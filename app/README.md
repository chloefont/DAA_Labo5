# Développement Android Laboratoire n°5 - Application communicante
## Implémentation de l'enrollment
À l'appui du bouton d'enrollement, l'application va vider la base de données locale, effectuer un
GET sur l'endpoint /enroll et sauvegarder l'UUID retourné dans une variable de notre ContactsViewModel.
Cet UUID est également enregistré en tant que SharedPreference afin de le conserver entre les sessions.

## Création, modification et suppression de contacts
Nous avons ajouté deux champs aux contact afin de gérer la synchronisation avec le serveur. Le champ
status permet de savoir si le contact a été créé, modifié ou supprimé localement. Il peut prendre
les valeurs suivantes : OK, NEW, UPDATED, DELETED. Le champ remote_id permet de garder l'identifiant
de l'objet sur le serveur afin de pouvoir le modifier ou le supprimer. Lorsqu'un contact est ajouté,
le contact se voit attribuer un remote_id null et un status NEW. Lorsqu'un contact est modifié, le
contact obtient un status UPDATED. Lorsqu'un contact est supprimé, il aura un status DELETED.
Tout contact ayant un status différent de OK est dans un état *dirty* et sera synchronisé lors d'un
appui sur le bouton de synchronisation. À noter que si le device n'est pas connecté à internet, ce 
bouton n'a aucun effet.

## Synchronisation de tous les contacts
Lorsqu'un appui sur le bouton de synchronisation est effectué, l'application va parcourir tous les
contacts de la base de données locale et va effectuer les actions suivantes : il va vérifier le 
status du contact. Si le status est OK, il passe au contact suivant. Si le status est NEW, un POST
est effectué sur le endpoint /contacts. Si le status code de retour est 201, le status et le remote_id
du contact sont mis à jour en conséquent. Si le status est UPDATED, un PUT est effectué sur le endpoint
/contacts/{remote_id}. Si le status code de retour est 200, le status est mis à OK. Si le status est
DELETED, un DELETE est effectué sur le endpoint /contacts/{remote_id}. Si le status code de retour 
est un 204, le contact est supprimé de la base de données locale. Dans le cas ou le status code de 
retourn'est pas celui attendu, le contact reste dans son état *diryt* et sera synchronisé lors d'un
prochain appui sur le bouton de synchronisation.