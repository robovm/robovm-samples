#!/usr/bin/env ruby

sizes = [[57,""],[72,"-72"],[76,"-76"],[114,"@2x"],[120,"-60@2x"],[144,"-72@2x"],[152,"-76@2x"]]

sizes.each do |x| 
  `/Applications/Inkscape.app/Contents/Resources/bin/inkscape --export-png ios/resources/Icon#{x[1]}.png -w #{x[0]} "ContractR Icon.svg"`
end
