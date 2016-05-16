% New plots for the comparison of the Osculating to mean algorithms%
%Semi Major Axis%
global oe;
global oem;
global oemOrekit;
global timeVector;
plot(timeVector(2:end),oem(4,2:end), 'red');
hold on;
plot(timeVector(2:end),oemOrekit(4,2:end), 'blue');
%A = oe(6,2:end);
%plot(timeVector(2:end),A, 'green');
title('Eccentericity Long Term vs Time');
ylabel('ECC');
legend('MEMA','MEOR', 'OSC');
hold on;