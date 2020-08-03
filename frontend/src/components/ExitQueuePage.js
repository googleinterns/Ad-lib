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
 * Define ExitQueue component
 * @return {ExitQueue} ExitQueue component
 */
export default function ExitQueue() {
  const classes = useStyles();
  return (
    <div>
      <Card className={classes.content}>
        <CardContent>
          <h3>We have removed you from the matching queue.</h3>
          <p>Thank you for using Ad-lib, we hope you try again later at your
             convenience!</p>
        </CardContent>
      </Card>
    </div>
  );
}
