//# sourceURL=application.js

/*
    Copyright (C) 2015 Apple Inc. All Rights Reserved.
    See LICENSE.txt for this sample’s licensing information
    
    Abstract:
    The TVMLKit application.
*/

/**
 * @description The onLaunch callback is invoked after the application JavaScript
 * has been parsed into a JavaScript context. The handler is passed an object
 * that contains options passed in for launch. These options are defined in the
 * Swift or Objective-C client code. Options can be used to communicate to
 * your JavaScript code that data and as well as state information, like if the
 * the app is being launched in the background.
 *
 * The location attribute is automatically added to the obejct and represents
 * the URL that was used to retrieve the application JavaScript.
 */
App.onLaunch = function(options) {

    /**
     * In this example we are passing the server BASEURL as a property
     * on the options object.
     */
    var BASEURL = options.BASEURL;

    /**
     * evaluateScripts is responsible for loading the JavaScript files neccessary
     * for you app to run. It can be used at any time in your apps lifecycle.
     *
     * @param - Array of JavaScript URLs
     * @param - Function called when the scripts have been evaluated. A boolean is
     * passed that indicates if the scripts were evaluated successfully.
     */
    var scripts = [`${BASEURL}/templates/Index.xml.js`];
    evaluateScripts(scripts, function(success) {
        if (success) {

            /*
            The XML template must be turned into a DOMDocument in order to be
            presented to the user. In this example we are utilizing the
            DOMParser to transform the Index template from a string representation
            into a DOMDocument.
            */
            var index = Template(),
                PARSER = new DOMParser(),
                doc = PARSER.parseFromString(index, "application/xml");

            /*
            Event listeners are used to handle and process user actions or events. Listeners
            can be added to the document or to each element. Events are bubbled up through the
            DOM heirarchy and can be handled or cancelled at at any point.

            Listeners can be added before or after the document has been presented.

            For a complete list of available events, see the TVMLKit DOM Documentation.
            */
            doc.addEventListener("select", startPlayback);
            doc.addEventListener("play", startPlayback);

            /*
            The document can be presented on screen by adding to to the documents array
            of the navigationDocument. The navigationDocument allows you to manipulate
            the documents array with the pushDocument, popDocument, replaceDocument, and
            removeDocument functions.

            You can also present a document in a modal view by using the pushModal() and
            removeModal() functions. Only a single document may be presented as a modal at
            any given time.
            */
            navigationDocument.pushDocument(doc);
        } else {

            /*
            Be sure to handle error cases in your code. In this example we are only
            throwing an error, in general you should present the error to the user
            as a dialog.

            See alertDialog for details.
            */
            var error = new Error("Playback Example: unable to evaluate scripts.");
            throw (error);
        }
    });
};

/**
 * @description
 * @param {Object} event - The 'select' or 'play' event
 */
function startPlayback(event) {
    var id = event.target.getAttribute("id"),
        videos = Videos[id];

    /*
    In TVMLKit, playback is handled entirely from JavaScript. The TVMLKit Player
    handles both audio and video MediaItems in any format supported by AVPlayer. You
    can also mix MediaItems of either type or format in the Player's Playlist.
    */
    var player = new Player();

    /*
    The playlist is an array of MediaItems. Each player must have a playlist,
    even if you only intend to play a single asset.
    */
    player.playlist = new Playlist();

    videos.forEach(function(metadata) {
        /*
        MediaItems are instantiated by passing two arguments to the MediaItem
        contructor, media type as a string ('video', 'audio') and the url for
        the asset itself.
        */
        var video = new MediaItem('video', metadata.url);

        /*
        You can set several properties on the MediaItem. Some properities are
        informational and are used to present additional information to the
        user. Other properties will determine the behavior of the player.

        For a full list of available properties, see the TVMLKit documentation.
        */
        video.title = metadata.title;
        video.subtitle = metadata.subtitle;
        video.description = metadata.description;
        video.artworkImageURL = metadata.artworkImageURL;

        /*
        ContentRatingDomain and contentRatingRanking are used together to enforce
        parental controls. If Parental Controls have been set for the device and
        the contentRatingRanking is higer than the device setting, the user will
        be prompted to enter their device Parental PIN Code in order to play the
        current asset.

        */
        video.contentRatingDomain = metadata.contentRatingDomain;
        video.contentRatingRanking = metadata.contentRatingRanking;

        /*
        The resumeTime is used to communicate the time at which a user previously stopped
        watching this asset, a bookmark. If this property is present the user will be
        prompted to resume playback from the point or start the asset over.

        resumeTime is the number of seconds from the beginning of the asset.
        */
        video.resumeTime = metadata.resumeTime;

        /*
        The MediaItem can be added to the Playlist with the push function.
        */
        player.playlist.push(video);
    });

    /*
    This function is a convenience function used to set listeners for various playback
    events.
    */
    setPlaybackEventListeners(player);

    /*
    Once the Player is ready, playback is started by calling the play function on the
    Player instance.
    */
    player.play();
}

/**
 * @description Sets playback event listeners on the player
 * @param {Player} currentPlayer - The current Player instance
 */
function setPlaybackEventListeners(currentPlayer) {

    /**
     * The requestSeekToTime event is called when the user attempts to seek to a specific point in the asset.
     * The listener is passed an object with the following attributes:
     * - type: this attribute represents the name of the event
     * - target: this attribute represents the event target which is the player object
     * - timeStamp: this attribute represents the timestamp of the event
     * - currentTime: this attribute represents the current playback time in seconds
     * - requestedTime: this attribute represents the time to seek to in seconds
     * The listener must return a value:
     * - true to allow the seek
     * - false or null to prevent it
     * - a number representing an alternative point in the asset to seek to, in seconds
     * @note Only a single requestSeekToTime listener can be active at any time. If multiple eventListeners are added for this event, only the last one will be called.
     */
    currentPlayer.addEventListener("requestSeekToTime", function(event) {
        console.log("Event: " + event.type + "\ntarget: " + event.target + "\ntimestamp: " + event.timeStamp + "\ncurrent time: " + event.currentTime + "\ntime to seek to: " + event.requestedTime) ;
        return true;
    });


    /**
     * The shouldHandleStateChange is called when the user requests a state change, but before the change occurs.
     * The listener is passed an object with the following attributes:
     * - type: this attribute represents the name of the event
     * - target: this attribute represents the event target which is the player object
     * - timeStamp: this attribute represents the name of the event
     * - state: this attribute represents the state that the player will switch to, possible values: playing, paused, scanning
     * - oldState: this attribute represents the previous state of the player, possible values: playing, paused, scanning
     * - elapsedTime: this attribute represents the elapsed time, in seconds
     * - duration: this attribute represents the duration of the asset, in seconds
     * The listener must return a value:
     * - true to allow the state change
     * - false to prevent the state change
     * This event should be handled as quickly as possible because the user has already performed the action and is waiting for the application to respond.
     * @note Only a single shouldHandleStateChange listener can be active at any time. If multiple eventListeners are added for this event, only the last one will be called.
     */
    currentPlayer.addEventListener("shouldHandleStateChange", function(event) {
        console.log("Event: " + event.type + "\ntarget: " + event.target + "\ntimestamp: " + event.timeStamp + "\nold state: " + event.oldState + "\nnew state: " + event.state + "\nelapsed time: " + event.elapsedTime + "\nduration: " + event.duration);
        return true;
    });

    /**
     * The stateDidChange event is called after the player switched states.
     * The listener is passed an object with the following attributes:
     * - type: this attribute represents the name of the event
     * - target: this attribute represents the event target which is the player object
     * - timeStamp: this attribute represents the timestamp of the event
     * - state: this attribute represents the state that the player switched to
     * - oldState: this attribute represents the state that the player switched from
     */
    currentPlayer.addEventListener("stateDidChange", function(event) {
        console.log("Event: " + event.type + "\ntarget: " + event.target + "\ntimestamp: " + event.timeStamp + "\noldState: " + event.oldState + "\nnew state: " + event.state);
    });

    /**
     * The stateWillChange event is called when the player is about to switch states.
     * The listener is passed an object with the following attributes:
     * - type: this attribute represents the name of the event
     * - target: this attribute represents the event target which is the player object
     * - timeStamp: this attribute represents the timestamp of the event
     * - state: this attribute represents the state that the player switched to
     * - oldState: this attribute represents the state that the player switched from
     */
    currentPlayer.addEventListener("stateWillChange", function(event) {
        console.log("Event: " + event.type + "\ntarget: " + event.target + "\ntimestamp: " + event.timeStamp + "\noldState: " + event.oldState + "\nnew state: " + event.state);
    });

    /**
     * The timeBoundaryDidCross event is called every time a particular time point is crossed during playback.
     * The listener is passed an object with the following attributes:
     * - type: this attribute represents the name of the event
     * - target: this attribute represents the event target which is the player object
     * - timeStamp: this attribute represents the timestamp of the event
     * - boundary: this attribute represents the boundary value that was crossed to trigger the event
     * When adding the listener, a third argument has to be provided as an array of numbers, each representing a time boundary as an offset from the beginning of the asset, in seconds.
     * @note This event can fire multiple times for the same time boundary as the user can scrub back and forth through the asset.
     */
    currentPlayer.addEventListener("timeBoundaryDidCross", function(event) {
        console.log("Event: " + event.type + "\ntarget: " + event.target + "\ntimestamp: " + event.timeStamp + "\nboundary: " + event.boundary);
    }, [30, 100, 150.5, 180.75]);

    /**
     * The timeDidChange event is called whenever a time interval has elapsed, this interval must be provided as the third argument when adding the listener.
     * The listener is passed an object with the following attributes:
     * - type: this attribute represents the name of the event
     * - target: this attribute represents the event target which is the player object
     * - timeStamp: this attribute represents the timestamp of the event
     * - time: this attribute represents the current playback time, in seconds.
     * - interval: this attribute represents the time interval
     * @note The interval argument should be an integer value as floating point values will be coerced to integers. If omitted, this value defaults to 1
     */
    currentPlayer.addEventListener("timeDidChange", function(event) {
        console.log("Event: " + event.type + "\ntarget: " + event.target + "\ntimestamp: " + event.timeStamp + "\ntime: " +  event.time + "\ninterval: " + event.interval);
    }, { interval: 10 });

    /**
     * The mediaItemDidChange event is called after the player switches media items.
     * The listener is passed an event object with the following attributes:
     * - type: this attribute represents the name of the event
     * - target: this attribute represents the event target which is the player object
     * - timeStamp: this attribute represents the timestamp of the event
     * - reason: this attribute represents the reason for the change; possible values are: 0 (Unknown), 1 (Played to end), 2 (Forwarded to end), 3 (Errored), 4 (Playlist changed), 5 (User initiated)
     */
    currentPlayer.addEventListener("mediaItemDidChange", function(event) {
        console.log("Event: " + event.type + "\ntarget: " + event.target + "\ntimestamp: " + event.timeStamp + "\nreason: " + event.reason);
    });

   /**
     * The mediaItemWillChange event is when the player is about to switch media items.
     * The listener is passed an event object with the following attributes:
     * - type: this attribute represents the name of the event
     * - target: this attribute represents the event target which is the player object
     * - timeStamp: this attribute represents the timestamp of the event
     * - reason: this attribute represents the reason for the change; possible values are: 0 (Unknown), 1 (Played to end), 2 (Forwarded to end), 3 (Errored), 4 (Playlist changed), 5 (User initiated)
     */
    currentPlayer.addEventListener("mediaItemWillChange", function(event) {
        console.log("Event: " + event.type + "\ntarget: " + event.target + "\ntimestamp: " + event.timeStamp + "\nreason: " + event.reason);
    });
}

/**
 * An object to store videos and playlists for ease of access
 */
var Videos = {
    video: [{
        title: "AV BipBop",
        subtitle: "Sample HLS Stream",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        artworkImageURL: "",
        contentRatingDomain: "movie",
        contentRatingRanking: 400,
        url: "https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8"
    }],

    playlist: [{
        title: "Apple Special Events",
        subtitle: "September 9, 2015",
        description: "Check out iPhone 6s and iPhone 6s Plus, learn about the powerful iPad Pro, take a look at the new features and bands for Apple Watch, and see the premiere of the all-new Apple TV.",
        artworkImageURL: "http://images.apple.com/apple-events/static/apple-events/apple-events-index/hero/september2015/hero_image_large.jpg",
        contentRatingDomain: "tvshow",
        contentRatingRanking: 200,
        url: "http://p.events-delivery.apple.com.edgesuite.net/1509pijnedfvopihbefvpijlkjb/m3u8/hls_vod_mvp.m3u8"
    }, {
        title: "Apple WWDC 2015 Keynote Address",
        subtitle: "June 8, 2015",
        description: "See the announcement of Apple Music, get a preview of OS X El Capitan and iOS 9, and learn what's next for Apple Watch and developers.",
        artworkImageURL: "http://images.apple.com/apple-events/static/apple-events/apple-events-index/pastevents/june2015/hero_image_large.jpg",
        contentRatingDomain: "tvshow",
        contentRatingRanking: 500,
        resumeTime: 330,
        url: "http://p.events-delivery.apple.com.edgesuite.net/15pijbnaefvpoijbaefvpihb06/m3u8/hls_vod_mvp.m3u8"
    }, {
        title: "Apple Special Event",
        subtitle: "March 2015",
        description: "Get an in-depth look at Apple Watch, witness the unveiling of the new MacBook, and learn about the innovations in ResearchKit.",
        artworkImageURL: "http://images.apple.com/apple-events/static/apple-events/apple-events-index/pastevents/march2015/hero_image_large.jpg",
        contentRatingDomain: "tvshow",
        contentRatingRanking: 200,
        url: "http://p.events-delivery.apple.com.edgesuite.net/1503ohibasdvoihbasfdv/vod/1503poihbsdfvpihb_cc_vod.m3u8"
    }]
};
