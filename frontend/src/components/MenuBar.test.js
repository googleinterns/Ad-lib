import React from 'react';
import MenuBar from './MenuBar';
import renderer from 'react-test-renderer';

describe('Menu Bar', () => {
  it('should be defined', () => {
    expect(MenuBar).toBeDefined();
  });
  test('should render correctly', () => {
    const tree = renderer.create(<MenuBar />).toJSON();
    expect(tree).toMatchSnapshot();
  });
});
