import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import HelpIcon from '@material-ui/icons/Help';

/**
 * Establishes style to use on rendering menu bar component
 */
const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  menuButton: {
    marginRight: theme.spacing(2),
  },
  title: {
    flexGrow: 1,
  },
}));

/**
 * Create menu bar component with title and help icon
 * @return {MenuBar} MenuBar component
 */
export default function MenuAppBar() {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" className={classes.title}>
            Ad-lib
          </Typography>
          {
            <div>
              <IconButton
                aria-label="help-icon"
                aria-controls="help-appbar"
                color="inherit"
              >
                <HelpIcon />
              </IconButton>
            </div>
          }
        </Toolbar>
      </AppBar>
    </div>
  );
}
