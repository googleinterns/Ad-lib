import React from "react";
import 'date-fns';
import DateFnsUtils from '@date-io/date-fns';
import { MuiPickersUtilsProvider, KeyboardTimePicker } from '@material-ui/pickers';
import { makeStyles } from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import MatchPreference from './MatchPreference';
import Checkbox from './PreferencesCheckbox';
import RoleDropdown from './RoleDropdown';
import YearDropdown from './YearsDropdown';
import ProductAreaDropdown from './ProductAreaDropdown';
import DurationDropdown from './DurationDropdown'

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
    width: 650
  },
  about: {
    margin: theme.spacing(4),
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
  const [savePreference, setSavePreference] = React.useState(true);
  const [matchPreference, setMatchPreference] = React.useState('none');

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
        <h4>About me...</h4>
      </div>
      <div className={classes.section}>
        <div className={classes.divStyle}>
            <RoleDropdown className={classes.inputField} onChange={value => setRole(value)} />
            <ProductAreaDropdown className={classes.inputField} onChange={value => setProductArea(value)} />
            <YearDropdown className={classes.inputField} onChange={value => setYearRange(value)} />
        </div>
        <div className={classes.divStyle}>
          <p>Match preference</p>
          <MatchPreference onChange={value => setMatchPreference(value)} />
        </div>
      </div>
      <div>
        <Checkbox className={classes.inputField} onChange={value => setSavePreference(value)} />
        <div className={classes.divStyle}>
          { /* TO-DO: change alert to JSON object that is sent over to backend */ }
          <Button variant="contained" color="primary" onClick={() => { 
            alert('Availability: ' + timeAvailableUntil + '\nDuration: ' + duration + '\nRole: ' + role +
            '\nPA: ' + productArea + '\nYears: ' + yearRange + '\nSave: ' + savePreference + '\nMatch: ' + matchPreference)}}>
            Submit
          </Button>
        </div>
      </div>
    </div>
  );
}