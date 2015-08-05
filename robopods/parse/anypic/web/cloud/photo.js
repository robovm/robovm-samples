// Validate Photos have a valid owner in the "user" pointer.
Parse.Cloud.beforeSave('Photo', function(request, response) {
  var currentUser = request.user;
  var objectUser = request.object.get('user');

  if(!currentUser || !objectUser) {
    response.error('A Photo should have a valid user.');
  } else if (currentUser.id === objectUser.id) {
    response.success();
  } else {
    response.error('Cannot set user on Photo to a user other than the current user.');
  }
});
