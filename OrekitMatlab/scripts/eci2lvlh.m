function [R] = eci2lvlh(reci, veci)
yaw = -reci/norm(reci);
temp = cross(veci,reci);
pitch = temp/norm(temp);
roll = cross(pitch, yaw);
R = [roll'; pitch'; yaw'];
% rlvlh = R*reci;
% vlvlh = R*veci;
% xvlvlh = R*xveci;
end