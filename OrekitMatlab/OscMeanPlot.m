% New plots for the comparison of the Osculating to mean algorithms%
%Semi Major Axis%
plot(timeVector(2:end),oem(2,2:end), 'red');
hold on;
plot(timeVector(2:end),oemOrekit(2,2:end), 'blue');
A = oe(2,2:end);
plot(timeVector(2:end),A, 'green');
title('Eccentericity Long Term vs Time');
ylabel('ECC');
legend('MEMA','MEOR', 'OSC');
hold on;