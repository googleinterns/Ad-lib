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
import PropTypes from 'prop-types';
import axios from 'axios';
import {makeStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Button from '@material-ui/core/Button';

/**
 * Establishes style to use on rendering components
 */
const useStyles = makeStyles((theme) => ({
  content: {
    margin: theme.spacing(2),
    width: 800,
  },
}));

// Add onExitQueueRequest to props validation
LoadingPage.propTypes = {
  onExitQueueRequest: PropTypes.func,
};

/** Send remove participant POST request to servlet
 * @return {Promise} promise
*/
export function sendPostRequest() {
  const removeParticipantRequest = 'Remove Participant';
  return axios.post('/api/v1/remove-participant', {removeParticipantRequest});
}

/**
 * Define LoadingPage component
 * @param {Object} props
 * @return {LoadingPage} LoadingPage component
 */
export default function LoadingPage(props) {
  const classes = useStyles();

  /** Send POST request to backend to remove participant
    * @param {Event} event
   */
  function handleExitQueueRequest(event) {
    // Override browser's default behvaior to execute POST request
    event.preventDefault();

    const postRequest = sendPostRequest();
    postRequest.then((response) => {
      if (response.data != null) {
        props.onExitQueueRequest();
      }
    });
  }

  return (
    <div>
      <Card className={classes.content}>
        <CardContent>
          <h3>Finding you a match...</h3>
          <p><em>This part may take some time as we wait for more Googlers
              to enter the queue.</em></p>
          <p>While you’re waiting on this page, you can continue working,
              learn about <a href="https://guidetoallyship.com/">Allyship</a>,
              or even make a smoothie! We will send you an <b>email </b>
              and <b>Calendar invite</b> as soon as we find you a match!</p>
          <Button variant="contained" color="primary"
            onClick={handleExitQueueRequest}>
            Exit Queue
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
