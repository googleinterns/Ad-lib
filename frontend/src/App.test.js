import React from 'react';
import App from './App';
import renderer from 'react-test-renderer';

beforeAll(() => {
  const DATE_TO_USE = new Date('2020');
  const mockedDate = Date;
  global.Date = jest.fn(() => DATE_TO_USE);
  global.Date.UTC = mockedDate.UTC;
  global.Date.parse = mockedDate.parse;
  global.Date.now = mockedDate.now;
});

describe('App', () => {
  it('should be defined', () => {
    expect(App).toBeDefined();
  });
  it('should render correctly', () => {
    const tree = renderer.create(<App />).toJSON();
    expect(tree).toMatchSnapshot();
  });
});
