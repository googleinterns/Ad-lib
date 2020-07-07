import React from "react";
import 'date-fns';
import DateFnsUtils from '@date-io/date-fns';
import { MuiPickersUtilsProvider, KeyboardTimePicker } from '@material-ui/pickers';
import { makeStyles } from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import Switch from './switch';
import Checkbox from './checkbox';
import RoleDropdown from './role-dropdown';
import YearDropdown from './years-dropdown';
import ProductAreaDropdown from './pa-dropdown';
import DurationDropdown from './duration-dropdown'

const useStyles = makeStyles(theme => ({
  inputField: {
    width: 180
  },
  divStyle: {
    display: 'flex',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    margin: theme.spacing(2)
  },
  section: {
    margin: theme.spacing(2),
    width: 480
  },
  about: {
    margin: theme.spacing(4),
    width: 480
  }
}));

// Create basic form component with availability and personal input fields and submit button
export default function Form() {
  const classes = useStyles();

  // Declare new state variables and set default states 
  const [timeAvailableUntil, setTimeAvailableUntil] = React.useState(new Date());
  const [duration, setDuration] = React.useState(15);
  const [productArea, setProductArea] = React.useState('');
  const [role, setRole] = React.useState('');
  const [yearRange, setYearRange] = React.useState('');

  return (
    <div>
      <div className={classes.section}>
        <div className={classes.divStyle}>
          <p>I'm free until...</p>
          <div className={classes.inputField}>
            <MuiPickersUtilsProvider utils={DateFnsUtils}>
              <KeyboardTimePicker
                id="time-field"
                value={timeAvailableUntil}
                onChange={value => setTimeAvailableUntil(value)}
                KeyboardButtonProps={{ 'aria-label': 'time-field' }}
              />
            </MuiPickersUtilsProvider>
          </div>
        </div>
        <div className={classes.divStyle}>
          <p>I want to talk for...</p>
          <DurationDropdown onChange={value => setDuration(value)} />
        </div>
      </div>  
      <div className={classes.about}>
        <h4>About me:</h4>
        <RoleDropdown className={classes.inputField} onChange={value => setRole(value)} />
        <ProductAreaDropdown className={classes.inputField} onChange={value => setProductArea(value)} />
        <YearDropdown className={classes.inputField} onChange={value => setYearRange(value)} />
      </div>
      <div className={classes.section}>
        <div className={classes.divStyle}>
          <p>Match me with a similar Googler</p>
          <Switch className={classes.inputField}/>
        </div>
      </div>
      <div>
        <Checkbox className={classes.inputField}/>
        <div className={classes.divStyle}>
          { /* TO-DO: change alert to JSON object that is sent over to backend */ }
          <Button variant="contained" color="primary" onClick={() => { 
            alert('Availability: ' + timeAvailableUntil + '\nDuration: ' + duration + 
            '\nRole: ' + role + '\nPA: ' + productArea + '\nYears: ' + yearRange)}}>
            Submit
          </Button>
        </div>
      </div>
    </div>
  );
}