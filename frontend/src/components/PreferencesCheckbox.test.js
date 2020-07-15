import React from 'react';
import PreferencesCheckbox from './PreferencesCheckbox';
import renderer from 'react-test-renderer';


describe('Preferences Checkbox', () => {
  it('should be defined', () => {
    expect(PreferencesCheckbox).toBeDefined();
  });
  it('should render correctly', () => {
    const tree = renderer.create(<PreferencesCheckbox />).toJSON();
    expect(tree).toMatchSnapshot();
  });
});