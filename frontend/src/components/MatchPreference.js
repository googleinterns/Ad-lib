import React from 'react';
import PropTypes from 'prop-types';
import Radio from '@material-ui/core/Radio';
import RadioGroup from '@material-ui/core/RadioGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import FormControl from '@material-ui/core/FormControl';

// Add onChange to props validation
MatchPreference.propTypes = {
  onChange: PropTypes.func,
};

/**
 * Create match preference radio group component
 * @return {MatchPreference} MatchPreference component
 * @param {Object} props
 */
export default function MatchPreference(props) {
  return (
    <FormControl component="fieldset">
      <RadioGroup
        row
        aria-label="matchPreference"
        name="matchPreference"
        defaultValue="none"
        onChange={(event) => props.onChange(event.target.value)}
      >
        <FormControlLabel
          value="similar"
          control={<Radio color="primary" />}
          label="Similar Googler" />
        <FormControlLabel
          value="none"
          control={<Radio color="primary" />}
          label="No Preference" />
        <FormControlLabel
          value="different"
          control={<Radio color="primary" />}
          label="Different Googler" />
      </RadioGroup>
    </FormControl>
  );
}