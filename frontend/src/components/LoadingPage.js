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

/**
 * Define LoadingPage component
 * @return {LoadingPage} LoadingPage component
 */
export default function LoadingPage() {
  const classes = useStyles();
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
          <Button variant="contained" color="primary">Exit Queue</Button>
        </CardContent>
      </Card>
    </div>
  );
}
