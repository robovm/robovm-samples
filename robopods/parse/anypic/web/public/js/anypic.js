$(function() {
  Parse.$ = jQuery;

  //  Copy your application id and JavaScript Key into the following line, and uncomment it.
    Parse.initialize("qFCDZFYvqQh8o362On3TS89BonXkTErHno9ES2VD", "ljD0uVrEHTP8oh2SS1fy2Vzxt1VaEvlFjoL0WHsv");

  // Photo Model
  // -----------

  var Photo = Parse.Object.extend("Photo", {
    photoURL: function() {
      return "#pic/" + this.id;
    },
    photoImageURL: function() {
      return this.get("image").url.replace(/http:\/\//,"https://s3.amazonaws.com/");
    },
    avatarImageURL: function() {
      if (this.get("user").get("profilePictureMedium")) {
        return this.get("user").get("profilePictureMedium").url.replace(/http:\/\//,"https://s3.amazonaws.com/");
      }
  
      return "";
    },
    displayName: function() {
      return this.get("user").get("displayName");
    }
  });
  

  // Photo Collection
  // ----------------
  
  var PhotoList = Parse.Collection.extend({
    model: Photo
  });


  // The Application  
  // ---------------

  // Photo View
  var PhotoView = Parse.View.extend({  
    template: _.template($("#photo-template").html()),
    render: function() {
      $(this.el).html(this.template({
        "photo_url": this.model.photoURL(),
        "photo_image_url": this.model.photoImageURL(),
        "avatar_image_url": this.model.avatarImageURL(),
        "display_name": this.model.displayName()      
      }));

      return this;
    }
  });
   
  // Latest Photos View
  var LatestPhotosView = Parse.View.extend({
    // Cache the template function for a single item.
    photosTemplate: _.template($("#latest-photos-template").html()),

    initialize: function() {
      var self = this;
      _.bindAll(this, 'addOne', 'addAll', 'render');

      this.$el.html(this.photosTemplate);

      // Create our collection of Photos
      this.photos = new PhotoList();

      // Only show photos uploaded by Parse employees
      var userQuery = new Parse.Query(Parse.User);
      userQuery.containedIn("objectId", [ "qOnj6eZPls", "AfQj8ksNwf", "46RvD6FsBS", "JDEuetLDXN", "Qi4eOJgPfi", "4MTgLcPMn2", "jX2d8bhb0S", "uco0D9h9KJ", "xgkncrHTZJ", "uTsAcCyPyZ", "7oTcL0tY1u", "kFk2qAJ4xO" ]);
      this.photos.query = new Parse.Query(Photo);
      this.photos.query.include("user");
      this.photos.query.matchesQuery("user", userQuery);

      this.photos.query.limit(8);
      this.photos.query.descending("createdAt");
  
      this.photos.bind('add', this.addOne);
      this.photos.bind('reset', this.addAll);
      this.photos.bind('all', this.render);

      // Fetch all the todo items for this user
      this.photos.fetch();

      this.spinnerView = new SpinnerView();
      this.$("#photo-list").html(this.spinnerView.render().el);
      this.$(".spinner").show();
    },

    render: function() {
      this.$("#photo-list").fadeIn();
      this.delegateEvents();
      return this;
    },

    addOne: function(photo) {
      var view = new PhotoView({model: photo});
      this.$("#photo-list").append(view.render().el);
    },
     
    // Add all items in the Todos collection at once.
    addAll: function(collection, filter) {
      this.$(".spinner").hide();
      this.photos.each(this.addOne);
    }
  });
  
  // Slider View
  var SliderView = Parse.View.extend({
    initialize: function() {
      var self = this;

      $("#slides").slides({
        preload: true,
        preloadImage: 'images/spinner.gif',
        play: 3000,
        pause: 2500,
        hoverPause: true
      });
    },

    render: function() {}
  });


  // Spinner View
  var SpinnerView = Parse.View.extend({
    template: _.template($("#spinner-template").html()),

    render: function() {
      $(this.el).html(this.template());
      return this;
    }
  });

  // Photo Landing Page View
  var PhotoLandingPageView = Parse.View.extend({
    template: _.template($("#landing-page-template").html()),

    initialize: function() {
      var self = this;
      _.bindAll(this, 'showPhoto', 'showError', 'render');
    },

    render: function() {
      $(this.el).html(this.template());

      // Show spinner
      this.spinnerView = new SpinnerView();
      this.$("#content").append(this.spinnerView.render().el);
      this.$(".spinner").show();
      return this;
    },

    showPhoto: function(photo) {
      this.$(".spinner").hide();
      this.photoView = new SinglePhotoView({model: photo});
      this.$("#content").append(this.photoView.render().el);
      this.$("#photo-container").fadeIn();
      // Update the Smart Banner metadata to include the fragment for this photo
      $("meta[name|='apple-itunes-app']").attr('content', "app-id=539741538, app-argument=/#pic/" + photo.id);
    },

    showError: function() {
      this.$(".spinner").hide();
      this.$("#content").html("Could not load picture");
    }
  });

  // Single Photo View, used to display the big photo in the landing page
  var SinglePhotoView = Parse.View.extend({

    template: _.template($("#big-photo-template").html()),
    metaDataLandingPageTemplate: _.template($("#meta-landingpage-template").html()),

    render: function() {
      $('head meta[property*="og:"]').remove();
      $('head').append(this.metaDataLandingPageTemplate({
        "photo_image_url": this.model.photoImageURL(),
        "photo_caption": "Shared a photo on Anypic",
        "page_url": "http://www.anypic.org/" + this.model.photoURL(),
      }));

      $(this.el).html(this.template({
        "photo_url": this.model.photoURL(),
        "photo_image_url": this.model.photoImageURL(),
        "avatar_image_url": this.model.avatarImageURL(),
        "display_name": this.model.displayName()
      }));

      return this;
    }
  });

  // Homepage View
  var HomePageView = Parse.View.extend({

    template: _.template($("#homepage-template").html()),
    metaDataHomePageTemplate: _.template($("#meta-homepage-template").html()),

    initialize: function() {
      var self = this;
    },

    render: function() {
      $('head meta[property*="og:"]').remove();
      $('head').append(this.metaDataHomePageTemplate());
      $(this.el).html(this.template());
      return this;
    }
  });

  // The main view for the app
  var AppView = Parse.View.extend({  
    // Instead of generating a new element, bind to the existing skeleton of
    // the App already present in the HTML.

    el: $("#main"), // TODO: @james: we should probably pass in this element at time of instantiation of AppView.. so we know that the html element exists (afaik, I think that is best practice too)

    initialize: function() {
      var self = this;
      _.bindAll(this, 'showLandingPage', 'showHomePage', 'render');

      this.homePageView = new HomePageView();
      this.latestPhotosView = new LatestPhotosView();
      this.landingPageView = new PhotoLandingPageView();
      this.render();
    },
  
    render: function() {
      return this;
    },

    showHomePage: function() {
      // Load landing page template
      $(this.el).html(this.homePageView.render().el);

      this.sliderView = new SliderView();
  
      // Fade out landing page if it's viible
      if (this.$("#landingpage").is(':visible')) {
        this.$("#landingpage").fadeOut();
      } else {
        this.$("#landingpage").hide();
      }

      this.$("#homepage").show();
      this.$("#slides").show();

      this.$(".downloadbutton").bind("click", function() {
        window.location = "http://itunes.apple.com/us/app/anypic/id539741538?ls=1&mt=8";
      });

      // clear out landingpage
      this.$("#content").html("");
 
      if (!this.$("#photo-list .photo-tumb").is(':visible')) {
        this.$("#latest").html(this.latestPhotosView.render().el);
        this.$("#latest").fadeIn();
      }

      // Reset the Smart Banner metadata
      $("meta[name|='apple-itunes-app']").attr('content', "app-id=539741538");
    },

    showLandingPage: function() {
      // Load landing page template
      $(this.el).html(this.landingPageView.render().el);

      // Fade in landing page view if we're switching from homepage
      if (this.$("#homepage").is(':visible')) {
        this.$("#landingpage").fadeIn();
      } else {
        this.$("#landingpage").show();
      }

      // Hide irrelevant views
      this.$("#homepage").hide();
      this.$("#slides").hide();
    }
  });

  var App = new AppView();

  var AppRouter = Backbone.Router.extend({
    routes: {
      "pic/:object_id": "getPic",
      "*actions": "defaultRoute"
    },

    getPic: function(object_id) {
      App.showLandingPage();

      var query = new Parse.Query(Photo);
      query.include("user");
      query.get(object_id, {
        success: function(photo) {
          App.landingPageView.showPhoto(photo);
        },
        error: function(object, error) {
          console.error(error);
          // The object was not retrieved successfully.
          // error is a Parse.Error with an error code and description.
          App.landingPageView.showError();
        }
      });
    },

    defaultRoute: function(actions) {
      App.showHomePage();
    }
  });
  
  // Initiate the router
  var app_router = new AppRouter();
   
  // Start Backbone history
  Backbone.history.start();

});