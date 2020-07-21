import React from 'react';
import PropTypes from 'prop-types';
import Input from '@material-ui/core/Input';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import ListItemText from '@material-ui/core/ListItemText';
import Select from '@material-ui/core/Select';
// import Checkbox from '@material-ui/core/Checkbox';

const interestsList = [
  'Anime/Manga',
  'Art & Design',
  'Books',
  'Current Events',
  'Entertainment',
  'Exercise/Fitness',
  'Food',
  'Gaming',
  'Mental Health',
  'Mindfulness',
  'Music',
  'Nature',
  'Pets',
  'Public Speaking',
  'Shopping',
  'Social Activism',
  'Sports',
  'Travel',
  'Volunteering',
  'Writing',
];

// Add onChange to props validation
InterestsDropdown.propTypes = {
  onChange: PropTypes.func,
};

/**
 * Create interests dropdown component using interests array
 * @param {Object} props
 * @return {InterestsDropdown} InterestsDropdown component
 */
export default function InterestsDropdown(props) {
  const [interests, interestsSelected] = React.useState([]);

  const handleChange = (event) => {
    const interest = event.target.value;
    props.onChange(interest);
    interestsSelected(interest);
  };

  return (
    <div>
      <FormControl style={{width: 180}}>
        <InputLabel id="interests">Interests</InputLabel>
        <Select
          id="interests"
          name="interests"
          multiple
          value={interests}
          onChange={handleChange}
          input={<Input />}
          renderValue={(selected) => selected.join(', ')}
          inputProps={{'aria-label': 'interests'}}
        >
          {interestsList.map((currentInterest) => (
            <MenuItem key={currentInterest} value={currentInterest}>
              <ListItemText primary={currentInterest} />
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </div>
  );
}
