/*
 * Copyright (C) 2015 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import <AnswerMeSDK/AnswerMeSDK.h>
#import "AnswerMeViewController.h"
#import "TopicCell.h"

@interface AnswerMeViewController ()

@end

@implementation AnswerMeViewController {
  AMAnswerMeSDK* sdk;
  NSArray<AMTopic*> *topics;
}

- (void)viewDidLoad {
  [super viewDidLoad];
  
  topics = [NSArray array];
  self.searchBar.delegate = self;
  self->sdk = [AMAnswerMeSDK instance];
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
  NSString *query = searchBar.text;
  
  [self->sdk searchWithQuery:query onSuccess:^(NSArray<AMTopic *> *_topics) {
    self->topics = _topics;
    [self.tableView performSelectorOnMainThread:@selector(reloadData) withObject:nil waitUntilDone:NO];
  } onFailure:^(NSString *errorMsg) {
    // TODO: Error handling
    NSLog(@"errorMsg = %@", errorMsg);
  }];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
  TopicCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
  AMTopic *topic = [self->topics objectAtIndex:[indexPath row]];
  cell.text.text = topic.displayText;
  cell.icon.image = nil;
  
  // Load the icon URL asynchronously if there is one
  if (topic.icon && topic.icon.url && topic.icon.url.length > 0) {
    [[[NSURLSession sharedSession] dataTaskWithURL:[NSURL URLWithString:topic.icon.url] completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
      dispatch_async(dispatch_get_main_queue(), ^{
        cell.icon.contentMode = UIViewContentModeScaleAspectFill;
        cell.icon.image = [UIImage imageWithData:data];
      });
    }] resume];
  }
  return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
  return [self->topics count];
}

@end
