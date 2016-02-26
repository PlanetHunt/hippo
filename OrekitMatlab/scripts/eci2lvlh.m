function [R, rlvlh, vlvlh, xvlvlh] = eci2lvlh(reci, veci, xveci)
yaw = -reci/mag(reci);
temp = cross(veci,reci);
pitch = temp/mag(temp);
roll = cross(pitch, yaw);
R = [roll'; pitch'; yaw'];
rlvlh = R*reci;
vlvlh = R*veci;
xvlvlh = R*xveci;
end