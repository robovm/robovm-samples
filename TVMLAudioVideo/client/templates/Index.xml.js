/*
	Copyright (C) 2015 Apple Inc. All Rights Reserved.
	See LICENSE.txt for this sample’s licensing information
	
	Abstract:
	A list template shows a list of items on the right, such as movies or TV shows. Focus on one to see its related content on the left, such as its artwork or description.
*/
var Template = function() {
	return '<?xml version="1.0" encoding="UTF-8"?>' +
	'<document>' +
		'<listTemplate>' +
			'<list>' +
				'<header>' +
					'<title>Playback examples</title>' +
				'</header>' +
				'<section>' +
					'<listItemLockup id="video">' +
						'<title>Video</title>' +
					'</listItemLockup>' +
					'<listItemLockup id="playlist">' +
						'<title>Playlist</title>' +
					'</listItemLockup>' +
				'</section>' +
			'</list>' +
		'</listTemplate>' +
	'</document>';
}
