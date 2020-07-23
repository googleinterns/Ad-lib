import React from 'react';
import PropTypes from 'prop-types';
import {makeStyles} from '@material-ui/core/styles';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';

/** Establishes style to use on rendering component */
const useStyles = makeStyles((theme) => ({
  padding: {
    margin: theme.spacing(2),
    marginBottom: theme.spacing(3),
  },
}));

const durations = [
  {label: '15 minutes', value: 15},
  {label: '30 minutes', value: 30},
  {label: '45 minutes', value: 45},
  {label: '60 minutes', value: 60},
];

// Add onChange to props validation
DurationDropdown.propTypes = {
  onChange: PropTypes.func,
};

/**
 * Create duration dropdown component using durations array
 * @param {Object} props
 * @return {DurationDropdown} DurationDropdown component
 */
export default function DurationDropdown(props) {
  const classes = useStyles();

  return (
    <div>
      <FormControl style={{width: 180}}>
        <Select
          id="duration-input"
          name="duration"
          defaultValue={15}
          onChange={(event) => props.onChange(event.target.value)}
          inputProps={{'aria-label': 'duration'}}
        >
          {durations.map((currentDuration) => (
            <option
              className={classes.padding}
              key={currentDuration.label}
              value={currentDuration.value}
            >
              {currentDuration.label}
            </option>
          ))}
        </Select>
      </FormControl>
    </div>
  );
}
