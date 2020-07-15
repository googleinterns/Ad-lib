import {validateFormInputs} from './Form';

test('should return true', () => {
  window.alert = jest.fn();
  const role = 'Intern';
  const productArea = 'Platforms and Ecosystems';
  const timeAvailableUntilInMilliseconds = 1594846605591;
  const duration = 15;
  const currentTimeInMilliseconds = 1594801800000;
  expect(validateFormInputs(role, productArea, duration,
      timeAvailableUntilInMilliseconds, currentTimeInMilliseconds)).toBe(true);
});

test('should return false since Role is not specified', () => {
  window.alert = jest.fn();
  const role = '';
  const productArea = 'Platforms and Ecosystems';
  const timeAvailableUntilInMilliseconds = 1594846605591;
  const duration = 15;
  const currentTimeInMilliseconds = 1594801800000;
  expect(validateFormInputs(role, productArea, duration,
      timeAvailableUntilInMilliseconds, currentTimeInMilliseconds)).toBe(false);
});

test('should return false since PA is not specified', () => {
  window.alert = jest.fn();
  const role = 'Intern';
  const productArea = '';
  const timeAvailableUntilInMilliseconds = 1594846605591;
  const duration = 15;
  const currentTimeInMilliseconds = 1594801800000;
  expect(validateFormInputs(role, productArea, duration,
      timeAvailableUntilInMilliseconds, currentTimeInMilliseconds)).toBe(false);
});

test('should return false since time is incompatible', () => {
  window.alert = jest.fn();
  const role = 'Intern';
  const productArea = 'Core';
  const timeAvailableUntilInMilliseconds = 1594846605591;
  const duration = 15;
  const currentTimeInMilliseconds = 1594846605000;
  expect(validateFormInputs(role, productArea, duration,
      timeAvailableUntilInMilliseconds, currentTimeInMilliseconds)).toBe(false);
});
