import md5 from 'js-md5'

export default function saltMD5 (password) {
  const salt = 'asdwqAsd1q_sz0'
  //const str = password + salt
  const str = "" + salt.charAt(0) +
                    salt.charAt(4) + password +
                    salt.charAt(0) + salt.charAt(1);
  return md5(str)
}
