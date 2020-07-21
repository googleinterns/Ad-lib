import React from 'react';
import PropTypes from 'prop-types';
import InputLabel from '@material-ui/core/InputLabel';
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

const productAreas = [
  'Ads',
  'Area 120',
  'Cloud',
  'Commerce',
  'Community Efforts',
  'Core',
  'Corporate Engineering',
  'Devices and Services',
  'Geo',
  'Global Affairs',
  'Global Business & Operations',
  'Global Communications & Public Affairs',
  'Google - advisors',
  'Google Finance',
  'Health',
  'Jigsaw',
  'Learning & Education',
  'Marketing',
  'Next Billion Users',
  'Payments',
  'People Operations',
  'Platforms & Ecosystems',
  'REWS (Real Estate & Workplace Services)',
  'Research',
  'Search',
  'Waze',
  'Youtube',
];

// Add onChange to props validation
ProductAreaDropdown.propTypes = {
  onChange: PropTypes.func,
};

/**
 * Create product area dropdown component using productAreas array
 * @param {Object} props
 * @return {ProductAreaDropdown} ProductAreaDropdown component
 */
export default function ProductAreaDropdown(props) {
  const classes = useStyles();

  return (
    <div>
      <FormControl style={{width: 180}}>
        <InputLabel id="productArea">Product Area</InputLabel>
        <Select
          id="productArea-input"
          name="productArea"
          onChange={(event) => props.onChange(event.target.value)}
          inputProps={{'aria-label': 'productArea'}}
        >
          {productAreas.map((currentProductArea) => (
            <option
              className={classes.padding}
              key={currentProductArea}
              value={currentProductArea}
            >
              {currentProductArea}
            </option>
          ))}
        </Select>
      </FormControl>
    </div>
  );
}
