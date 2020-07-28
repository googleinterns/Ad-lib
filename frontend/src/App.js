// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import './App.css';
import MenuBar from './components/MenuBar.js';
import Form from './components/Form.js';

/**
 * Establishes style to use on rendering components
 */
const useStyles = makeStyles((theme) => ({
  centerHorizontal: {
    position: 'absolute', left: '50%',
    transform: 'translate(-50%)',
  },
  content: {
    margin: theme.spacing(2),
    width: 800,
  },
}));

/**
 * Add components and content to UI
 * @return {App} App component
 */
export default function App() {
  const classes = useStyles();

  return (
    <div>
      <MenuBar />
      <div className={classes.centerHorizontal}>
        <Card className={classes.content}>
          <CardContent>
            <h3>Meet fellow Googlers <em>now</em>!</h3>
            <p>Miss bumping into new faces at the office? Want an easy, fun,
               spontaneous way of meeting Googlers virtually? Now you can!</p>
            <p>Ad-lib matches you with a fellow Googler in the queue, notifies
               you through email when you’ve been matched, and adds an event to
               your Calendar with a Meet link for you to join immediately! It
               also provides a starter question to get the conversation
               flowing!</p>
          </CardContent>
        </Card>
        <Card className={classes.content}>
          <Form />
        </Card>
      </div>
    </div>
  );
}
