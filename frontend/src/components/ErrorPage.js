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
 * Define ErrorPage component
 * @return {ErrorPage} ErrorPage component
 */
export default function ErrorPage() {
  const classes = useStyles();
  return (
    <div>
      <Card className={classes.content}>
        <CardContent>
          <h3>Oops, something went wrong</h3>
          <p>We encourage you to please try again later!</p>
        </CardContent>
      </Card>
    </div>
  );
}