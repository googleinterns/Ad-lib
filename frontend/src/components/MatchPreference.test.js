import React from 'react';
import MatchPreferences from './MatchPreference';
import renderer from 'react-test-renderer';

describe('Match Preferences Radio Group', () => {
  it('should be defined', () => {
    expect(MatchPreferences).toBeDefined();
  });
  it('should render correctly', () => {
    const tree = renderer.create(<MatchPreferences />).toJSON();
    expect(tree).toMatchSnapshot();
  });
});
