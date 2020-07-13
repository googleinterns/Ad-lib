(this['webpackJsonpcapstone-proj']=this['webpackJsonpcapstone-proj']||[]).push([[0], {104: function(e, a, t) {
  e.exports=t(133);
}, 109: function(e, a, t) {}, 110: function(e, a, t) {}, 133: function(e, a, t) {
  'use strict'; t.r(a); const n=t(0); const r=t.n(n); const l=t(10); const o=t.n(l); const i=(t(109), t(167)); const c=t(178); const u=t(179); const m=(t(110), t(170)); const s=t(172); const f=t(47); const d=t(134); const E=t(80); const p=t.n(E); const g=Object(i.a)((function(e) {
    return {root: {flexGrow: 1}, menuButton: {marginRight: e.spacing(2)}, title: {flexGrow: 1}};
  })); function v() {
    const e=g(); return r.a.createElement('div', {className: e.root}, r.a.createElement(m.a, {position: 'static'}, r.a.createElement(s.a, null, r.a.createElement(f.a, {variant: 'h6', className: e.title}, 'Ad-lib'), r.a.createElement('div', null, r.a.createElement(d.a, {'aria-label': 'help-icon', 'aria-controls': 'help-appbar', 'color': 'inherit'}, r.a.createElement(p.a, null))))));
  } const h=t(37); const b=t(81); const y=t.n(b); const w=t(83); const C=t(13); const S=t(180); const N=t(177); const P=t(181); const k=t(185); const x=t(174); const j=t(173); function D(e) {
    return r.a.createElement(j.a, {component: 'fieldset'}, r.a.createElement(k.a, {'row': !0, 'aria-label': 'matchPreference', 'name': 'matchPreference', 'defaultValue': 'none', 'onChange': function(a) {
      return e.onChange(a.target.value);
    }}, r.a.createElement(x.a, {value: 'similar', control: r.a.createElement(P.a, {color: 'primary'}), label: 'Similar Googler'}), r.a.createElement(x.a, {value: 'none', control: r.a.createElement(P.a, {color: 'primary'}), label: 'No Preference'}), r.a.createElement(x.a, {value: 'different', control: r.a.createElement(P.a, {color: 'primary'}), label: 'Different Googler'})));
  } const A=t(184); function G(e) {
    return r.a.createElement(x.a, {label: 'Save my preferences for later', labelPlacement: 'start', control: r.a.createElement(A.a, {onChange: function(a) {
      return e.onChange(a.target.checked);
    }, name: 'checkbox', color: 'primary', defaultChecked: !0})});
  } const O=t(187); const I=t(186); const B=t(176); const R=t(182); const M=['Software Engineer', 'Product Manager', 'UI/UX Designer', 'Recruiter', 'Intern', 'VP', 'Director', 'CEO']; function W(e) {
    return r.a.createElement('div', null, r.a.createElement(j.a, {style: {width: 180}}, r.a.createElement(O.a, {id: 'role'}, 'Role'), r.a.createElement(R.a, {id: 'role-input', name: 'role', onChange: function(a) {
      return e.onChange(a.target.value);
    }, inputProps: {'aria-label': 'role'}}, M.map((function(e) {
      return r.a.createElement(I.a, {key: e, value: e}, r.a.createElement(B.a, {primary: e}));
    })))));
  } const U=['Ads', 'Area 120', 'Cloud', 'Commerce', 'Community Efforts', 'Core', 'Corporate Engineering', 'Devices and Services', 'Geo', 'Global Affairs', 'Global Business & Operations', 'Global Communications & Public Affairs', 'Google - advisors', 'Google Finance', 'Health', 'Jigsaw', 'Learning & Education', 'Marketing', 'Next Billion Users', 'Payments', 'People Operations', 'Platforms & Ecosystems', 'REWS (Real Estate & Workplace Services)', 'Research', 'Search', 'Waze', 'Youtube']; function z(e) {
    return r.a.createElement('div', null, r.a.createElement(j.a, {style: {width: 180}}, r.a.createElement(O.a, {id: 'productArea'}, 'Product Area'), r.a.createElement(R.a, {id: 'productArea-input', name: 'productArea', onChange: function(a) {
      return e.onChange(a.target.value);
    }, inputProps: {'aria-label': 'productArea'}}, U.map((function(e) {
      return r.a.createElement(I.a, {key: e, value: e}, r.a.createElement(B.a, {primary: e}));
    })))));
  } const H=t(82); const J=[{label: '15 minutes', value: 15}, {label: '30 minutes', value: 30}, {label: '45 minutes', value: 45}, {label: '60 minutes', value: 60}]; function L(e) {
    return r.a.createElement('div', null, r.a.createElement(j.a, {style: {width: 180}}, r.a.createElement(H.a, {id: 'duration-input', name: 'duration', onChange: function(a) {
      return e.onChange(a.target.value);
    }, inputProps: {'aria-label': 'duration'}}, J.map((function(e) {
      return r.a.createElement('option', {key: e.label, value: e.value}, e.label);
    })))));
  } const T=Object(i.a)((function(e) {
    return {flexStartDiv: {display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', margin: e.spacing(2)}, flexEndDiv: {display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: e.spacing(2), marginBottom: e.spacing(1), marginRight: e.spacing(1)}, section: {margin: e.spacing(2), width: 650}, heading: {marginLeft: e.spacing(2)}, padding: {marginTop: e.spacing(4), marginLeft: e.spacing(2)}};
  })); function q() {
    const e=T(); const a=r.a.useState(new Date); const t=Object(h.a)(a, 2); const n=t[0]; const l=t[1]; const o=r.a.useState(15); const i=Object(h.a)(o, 2); const c=i[0]; const u=i[1]; const m=r.a.useState(''); const s=Object(h.a)(m, 2); const f=s[0]; const d=s[1]; const E=r.a.useState(''); const p=Object(h.a)(E, 2); const g=p[0]; const v=p[1]; const b=r.a.useState(!0); const P=Object(h.a)(b, 2); const k=P[0]; const x=P[1]; const j=r.a.useState('none'); const A=Object(h.a)(j, 2); const O=A[0]; const I=A[1]; return r.a.createElement('div', null, r.a.createElement('div', {className: e.heading}, r.a.createElement('h3', null, 'Choose your time preferences')), r.a.createElement('div', {className: e.section}, r.a.createElement('div', {className: e.flexStartDiv}, r.a.createElement('p', null, 'I am free until...'), r.a.createElement('div', {style: {width: 180}}, r.a.createElement(C.a, {utils: w.a}, r.a.createElement(S.a, {id: 'time-field', value: n, onChange: function(e) {
      return l(e);
    }, KeyboardButtonProps: {'aria-label': 'time-field'}})))), r.a.createElement('div', {className: e.flexStartDiv}, r.a.createElement('p', null, 'I want to talk for...'), r.a.createElement(L, {onChange: function(e) {
      return u(e);
    }}))), r.a.createElement('div', {className: e.heading}, r.a.createElement('h3', null, 'Choose your match preferences')), r.a.createElement('div', {className: e.section}, r.a.createElement('div', {className: e.flexStartDiv}, r.a.createElement(W, {onChange: function(e) {
      return v(e);
    }}), r.a.createElement(z, {onChange: function(e) {
      return d(e);
    }})), r.a.createElement('div', {className: e.padding}, r.a.createElement(D, {onChange: function(e) {
      return I(e);
    }}))), r.a.createElement('div', {className: e.flexEndDiv}, r.a.createElement(G, {onChange: function(e) {
      return x(e);
    }}), r.a.createElement('div', {className: e.flexEndDiv}, r.a.createElement(N.a, {variant: 'contained', color: 'primary', onClick: function(e) {
      e.preventDefault(); const a={timeAvailableUntil: n.getTime(), duration: c, role: g, productArea: f, matchPreference: O, savePreference: k}; console.log(a), y.a.post('/api/v1/add-participant', {formDetails: a}).then((function(e) {
        null!=e.data&&alert('Successful');
      }));
    }}, 'Submit'))));
  } const V=Object(i.a)((function(e) {
    return {centerHorizontal: {position: 'absolute', left: '50%', transform: 'translate(-50%)'}, content: {margin: e.spacing(2), width: 800}};
  })); function F() {
    const e=V(); return r.a.createElement('div', null, r.a.createElement(v, null), r.a.createElement('div', {className: e.centerHorizontal}, r.a.createElement(c.a, {className: e.content}, r.a.createElement(u.a, null, r.a.createElement('h2', null, 'Meet fellow Googlers ', r.a.createElement('em', null, 'now'), '!'), r.a.createElement('h4', null, 'Miss bumping into new faces at the office? Want an easy, fun, spontaneous way of meeting Googlers virtually? Now you can!'), r.a.createElement('h4', null, 'Ad-lib matches you with a fellow Googler in the queue, notifies you through email when you\u2019ve been matched, and adds an event to your Calendar with a Meet link for you to join immediately! It also provides a starter question to get the conversation flowing!'))), r.a.createElement(c.a, {className: e.content}, r.a.createElement(q, null))));
  }Boolean('localhost'===window.location.hostname||'[::1]'===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/)); o.a.render(r.a.createElement(r.a.StrictMode, null, r.a.createElement(F, null)), document.getElementById('root')), 'serviceWorker'in navigator&&navigator.serviceWorker.ready.then((function(e) {
    e.unregister();
  })).catch((function(e) {
    console.error(e.message);
  }));
}}, [[104, 1, 2]]]);
// # sourceMappingURL=main.3f06da45.chunk.js.map
