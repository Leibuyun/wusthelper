function W(e, t, n) {
    this.e = d(e),
        this.d = d(t),
        this.m = d(n),
        this.chunkSize = 2 * h(this.m),
        this.radix = 16,
        this.barrett = new I(this.m)
}

function a(e) {
    U = e,
        R = new Array(U);
    for (var t = 0; t < R.length; t++)
        R[t] = 0;
    F = new i,
        q = new i,
        q.digits[0] = 1
}

function i(e) {
    this.digits = "boolean" == typeof e && 1 == e ? null : R.slice(0),
        this.isNeg = !1
}

function r(e) {
    var t = new i(!0);
    return t.digits = e.digits.slice(0),
        t.isNeg = e.isNeg,
        t
}

function o(e) {
    var t = new i;
    t.isNeg = e < 0,
        e = Math.abs(e);
    for (var n = 0; e > 0;)
        t.digits[n++] = e & z,
            e = Math.floor(e / j);
    return t
}

function s(e) {
    for (var t = "", n = e.length - 1; n > -1; --n)
        t += e.charAt(n);
    return t
}

function c(e, t) {
    var n = new i;
    n.digits[0] = t;
    for (var a = C(e, n), r = J[a[1].digits[0]]; 1 == x(a[0], F);)
        a = C(a[0], n),
            digit = a[1].digits[0],
            r += J[a[1].digits[0]];
    return (e.isNeg ? "-" : "") + s(r)
}

function l(e) {
    for (var t = "", n = 0; n < 4; ++n)
        t += V[15 & e],
            e >>>= 4;
    return s(t)
}

function u(e) {
    for (var t = "", n = (h(e),
        h(e)); n > -1; --n)
        t += l(e.digits[n]);
    return t
}

function p(e) {
    return e >= 48 && e <= 57 ? e - 48 : e >= 65 && e <= 90 ? 10 + e - 65 : e >= 97 && e <= 122 ? 10 + e - 97 : 0
}

function m(e) {
    for (var t = 0, n = Math.min(e.length, 4), a = 0; a < n; ++a)
        t <<= 4,
            t |= p(e.charCodeAt(a));
    return t
}

function d(e) {
    for (var t = new i, n = e.length, a = n, r = 0; a > 0; a -= 4,
        ++r)
        t.digits[r] = m(e.substr(Math.max(a - 4, 0), Math.min(a, 4)));
    return t
}

function _(e, t) {
    var n = "-" == e.charAt(0)
        , a = n ? 1 : 0
        , r = new i
        , o = new i;
    o.digits[0] = 1;
    for (var s = e.length - 1; s >= a; s--) {
        r = f(r, v(o, p(e.charCodeAt(s)))),
            o = v(o, t)
    }
    return r.isNeg = n,
        r
}

function f(e, t) {
    var n;
    if (e.isNeg != t.isNeg)
        t.isNeg = !t.isNeg,
            n = g(e, t),
            t.isNeg = !t.isNeg;
    else {
        n = new i;
        for (var a, r = 0, o = 0; o < e.digits.length; ++o)
            a = e.digits[o] + t.digits[o] + r,
                n.digits[o] = a % j,
                r = Number(a >= j);
        n.isNeg = e.isNeg
    }
    return n
}

function g(e, t) {
    var n;
    if (e.isNeg != t.isNeg)
        t.isNeg = !t.isNeg,
            n = f(e, t),
            t.isNeg = !t.isNeg;
    else {
        n = new i;
        var a, r;
        r = 0;
        for (var o = 0; o < e.digits.length; ++o)
            a = e.digits[o] - t.digits[o] + r,
                n.digits[o] = a % j,
            n.digits[o] < 0 && (n.digits[o] += j),
                r = 0 - Number(a < 0);
        if (-1 == r) {
            r = 0;
            for (var o = 0; o < e.digits.length; ++o)
                a = 0 - n.digits[o] + r,
                    n.digits[o] = a % j,
                n.digits[o] < 0 && (n.digits[o] += j),
                    r = 0 - Number(a < 0);
            n.isNeg = !e.isNeg
        } else
            n.isNeg = e.isNeg
    }
    return n
}

function h(e) {
    for (var t = e.digits.length - 1; t > 0 && 0 == e.digits[t];)
        --t;
    return t
}

function E(e) {
    var t, n = h(e), a = e.digits[n], i = (n + 1) * B;
    for (t = i; t > i - B && 0 == (32768 & a); --t)
        a <<= 1;
    return t
}

function y(e, t) {
    for (var n, a, r, o = new i, s = h(e), c = h(t), l = 0; l <= c; ++l) {
        n = 0,
            r = l;
        for (var u = 0; u <= s; ++u,
            ++r)
            a = o.digits[r] + e.digits[u] * t.digits[l] + n,
                o.digits[r] = a & z,
                n = a >>> K;
        o.digits[l + s + 1] = n
    }
    return o.isNeg = e.isNeg != t.isNeg,
        o
}

function v(e, t) {
    var n, a, r, o = new i;
    n = h(e),
        a = 0;
    for (var s = 0; s <= n; ++s)
        r = o.digits[s] + e.digits[s] * t + a,
            o.digits[s] = r & z,
            a = r >>> K;
    return o.digits[1 + n] = a,
        o
}

function b(e, t, n, a, i) {
    for (var r = Math.min(t + i, e.length), o = t, s = a; o < r; ++o,
        ++s)
        n[s] = e[o]
}

function w(e, t) {
    var n = Math.floor(t / B)
        , a = new i;
    b(e.digits, 0, a.digits, n, a.digits.length - n);
    for (var r = t % B, o = B - r, s = a.digits.length - 1, c = s - 1; s > 0; --s,
        --c)
        a.digits[s] = a.digits[s] << r & z | (a.digits[c] & Y[r]) >>> o;
    return a.digits[0] = a.digits[s] << r & z,
        a.isNeg = e.isNeg,
        a
}

function T(e, t) {
    var n = Math.floor(t / B)
        , a = new i;
    b(e.digits, n, a.digits, 0, e.digits.length - n);
    for (var r = t % B, o = B - r, s = 0, c = s + 1; s < a.digits.length - 1; ++s,
        ++c)
        a.digits[s] = a.digits[s] >>> r | (a.digits[c] & H[r]) << o;
    return a.digits[a.digits.length - 1] >>>= r,
        a.isNeg = e.isNeg,
        a
}

function S(e, t) {
    var n = new i;
    return b(e.digits, 0, n.digits, t, n.digits.length - t),
        n
}

function O(e, t) {
    var n = new i;
    return b(e.digits, t, n.digits, 0, n.digits.length - t),
        n
}

function k(e, t) {
    var n = new i;
    return b(e.digits, 0, n.digits, 0, t),
        n
}

function x(e, t) {
    if (e.isNeg != t.isNeg)
        return 1 - 2 * Number(e.isNeg);
    for (var n = e.digits.length - 1; n >= 0; --n)
        if (e.digits[n] != t.digits[n])
            return e.isNeg ? 1 - 2 * Number(e.digits[n] > t.digits[n]) : 1 - 2 * Number(e.digits[n] < t.digits[n]);
    return 0
}

function C(e, t) {
    var n, a, o = E(e), s = E(t), c = t.isNeg;
    if (o < s)
        return e.isNeg ? (n = r(q),
            n.isNeg = !t.isNeg,
            e.isNeg = !1,
            t.isNeg = !1,
            a = g(t, e),
            e.isNeg = !0,
            t.isNeg = c) : (n = new i,
            a = r(e)),
            new Array(n, a);
    n = new i,
        a = e;
    for (var l = Math.ceil(s / B) - 1, u = 0; t.digits[l] < G;)
        t = w(t, 1),
            ++u,
            ++s,
            l = Math.ceil(s / B) - 1;
    a = w(a, u),
        o += u;
    for (var p = Math.ceil(o / B) - 1, m = S(t, p - l); -1 != x(a, m);)
        ++n.digits[p - l],
            a = g(a, m);
    for (var d = p; d > l; --d) {
        var _ = d >= a.digits.length ? 0 : a.digits[d]
            , y = d - 1 >= a.digits.length ? 0 : a.digits[d - 1]
            , b = d - 2 >= a.digits.length ? 0 : a.digits[d - 2]
            , O = l >= t.digits.length ? 0 : t.digits[l]
            , k = l - 1 >= t.digits.length ? 0 : t.digits[l - 1];
        n.digits[d - l - 1] = _ == O ? z : Math.floor((_ * j + y) / O);
        for (var C = n.digits[d - l - 1] * (O * j + k), A = _ * X + (y * j + b); C > A;)
            --n.digits[d - l - 1],
                C = n.digits[d - l - 1] * (O * j | k),
                A = _ * j * j + (y * j + b);
        m = S(t, d - l - 1),
            a = g(a, v(m, n.digits[d - l - 1])),
        a.isNeg && (a = f(a, m),
            --n.digits[d - l - 1])
    }
    return a = T(a, u),
        n.isNeg = e.isNeg != c,
    e.isNeg && (n = c ? f(n, q) : g(n, q),
        t = T(t, u),
        a = g(t, a)),
    0 == a.digits[0] && 0 == h(a) && (a.isNeg = !1),
        new Array(n, a)
}

function A(e, t) {
    return C(e, t)[0]
}

function I(e) {
    this.modulus = r(e),
        this.k = h(this.modulus) + 1;
    var t = new i;
    t.digits[2 * this.k] = 1,
        this.mu = A(t, this.modulus),
        this.bkplus1 = new i,
        this.bkplus1.digits[this.k + 1] = 1,
        this.modulo = N,
        this.multiplyMod = P,
        this.powMod = M
}

function N(e) {
    var t = O(e, this.k - 1)
        , n = y(t, this.mu)
        , a = O(n, this.k + 1)
        , i = k(e, this.k + 1)
        , r = y(a, this.modulus)
        , o = k(r, this.k + 1)
        , s = g(i, o);
    s.isNeg && (s = f(s, this.bkplus1));
    for (var c = x(s, this.modulus) >= 0; c;)
        s = g(s, this.modulus),
            c = x(s, this.modulus) >= 0;
    return s
}

function P(e, t) {
    var n = y(e, t);
    return this.modulo(n)
}

function M(e, t) {
    var n = new i;
    n.digits[0] = 1;
    for (var a = e, r = t; ;) {
        if (0 != (1 & r.digits[0]) && (n = this.multiplyMod(n, a)),
            r = T(r, 1),
        0 == r.digits[0] && 0 == h(r))
            break;
        a = this.multiplyMod(a, a)
    }
    return n
}

function W(e, t, n) {
    this.e = d(e),
        this.d = d(t),
        this.m = d(n),
        this.chunkSize = 2 * h(this.m),
        this.radix = 16,
        this.barrett = new I(this.m)
}

function D(e, t, n) {
    return new W(e, t, n)
}

function L(e, t) {
    for (var n = new Array, a = t.length, r = 0; r < a;)
        n[r] = t.charCodeAt(r),
            r++;
    for (; n.length % e.chunkSize != 0;)
        n[r++] = 0;
    var o, s, l, p = n.length, m = "";
    for (r = 0; r < p; r += e.chunkSize) {
        for (l = new i,
                 o = 0,
                 s = r; s < r + e.chunkSize; ++o)
            l.digits[o] = n[s++],
                l.digits[o] += n[s++] << 8;
        var d = e.barrett.powMod(l, e.e);
        m += (16 == e.radix ? u(d) : c(d, e.radix)) + " "
    }
    return m.substring(0, m.length - 1)
}

function Q(e, t) {
    var n, a, i, r = t.split(" "), o = "";
    for (n = 0; n < r.length; ++n) {
        var s;
        for (s = 16 == e.radix ? d(r[n]) : _(r[n], e.radix),
                 i = e.barrett.powMod(s, e.d),
                 a = 0; a <= h(i); ++a)
            o += String.fromCharCode(255 & i.digits[a], i.digits[a] >> 8)
    }
    return o.charCodeAt(o.length - 1),
        o
}

var U, R, F, q, K = 16, B = K, j = 65536, G = j >>> 1, X = j * j, z = j - 1;
a(20);
var J = (o(1e15),
        new Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"))
    , V = new Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f")
    ,
    Y = new Array(0, 32768, 49152, 57344, 61440, 63488, 64512, 65024, 65280, 65408, 65472, 65504, 65520, 65528, 65532, 65534, 65535)
    , H = new Array(0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535)


function myfun(t) {
    a(131)
    var h = D('010001', "", '00b5eeb166e069920e80bebd1fea4829d3d1f3216f2aabe79b6c47a3c18dcee5fd22c2e7ac519cab59198ece036dcf289ea8201e2a0b9ded307f8fb704136eaeb670286f5ad44e691005ba9ea5af04ada5367cd724b5a26fdb5120cc95b6431604bd219c6b7d83a6f8f24b43918ea988a76f93c333aa5a20991493d4eb1117e7b1');
    return L(h, t)
}